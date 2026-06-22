package com.foodly.radar.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodly.radar.application.dto.NearbyRestaurantDto;
import com.foodly.radar.domain.model.GeoLocation;
import com.foodly.radar.infrastructure.geospatial.H3RadarService;
import com.foodly.radar.infrastructure.persistence.RedisRadarRepository;
import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.inject.Inject;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;

@MessageDriven(name = "RestaurantEventListener", activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "jms/queue/BusinessMenusQueue"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Queue"),
    @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge")
})
public class RestaurantEventListener implements MessageListener {

    @Inject
    private H3RadarService h3RadarService;

    @Inject
    private RedisRadarRepository redisRadarRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                String jsonPayload = ((TextMessage) message).getText();
                System.out.println("[RadarService JMS] Evento recibido desde Business: " + jsonPayload);

                // 1. Deserializar el evento crudo que viene desde la cola a un DTO de negocio
                NearbyRestaurantDto restaurantEvent = objectMapper.readValue(jsonPayload, NearbyRestaurantDto.class);

                // 2. Validar que tenga coordenadas físicas válidas para el mapeo geoespacial
                if (restaurantEvent.getLatitude() != null && restaurantEvent.getLongitude() != null) {
                    GeoLocation location = new GeoLocation(restaurantEvent.getLatitude(), restaurantEvent.getLongitude());

                    // 3. Calcular matemáticamente su índice hexagonal H3 en caliente
                    String currentH3Index = h3RadarService.latLngToCell(location);
                    restaurantEvent.setH3Index(currentH3Index); // Inyectamos el índice calculado para el Front

                    // Convertimos el DTO enriquecido a JSON string optimizado para el caché
                    String updatedRestaurantJson = objectMapper.writeValueAsString(restaurantEvent);

                    // 4. Actualizar la celda en el Connection Pool de Redis
                    // (Si el restaurante ya existía, limpia la anterior e inserta el estado fresco del menú)
                    redisRadarRepository.removeFromCell(currentH3Index, restaurantEvent.getId());
                    redisRadarRepository.saveToCell(currentH3Index, restaurantEvent.getId(), updatedRestaurantJson);

                    System.out.println("[RadarService JMS] Caché de Redis actualizado con éxito para el Huarique: " 
                            + restaurantEvent.getName() + " en la celda H3: " + currentH3Index);
                }
            }
        } catch (Exception e) {
            // Logueamos el error de infraestructura de forma limpia en la consola negra de WildFly
            System.err.println("[RadarService JMS] Error crítico procesando evento de restaurante asíncrono: " + e.getMessage());
        }
    }
}