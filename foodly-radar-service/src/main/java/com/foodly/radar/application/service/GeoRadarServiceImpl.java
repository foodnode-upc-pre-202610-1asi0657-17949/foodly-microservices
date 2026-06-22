package com.foodly.radar.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodly.radar.application.dto.NearbyRestaurantDto;
import com.foodly.radar.application.dto.RadarSearchRequestDto;
import com.foodly.radar.domain.model.GeoLocation;
import com.foodly.radar.infrastructure.geospatial.H3RadarService;
import com.foodly.radar.infrastructure.persistence.RedisRadarRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class GeoRadarServiceImpl implements GeoRadarService {

    @Inject
    private H3RadarService h3RadarService;

    @Inject
    private RedisRadarRepository redisRadarRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<NearbyRestaurantDto> searchNearbyRestaurants(RadarSearchRequestDto request) {
        // 1. Crear el objeto de dominio y validar coordenadas básicas
        GeoLocation userLocation = new GeoLocation(request.getLatitude(), request.getLongitude());

        // 2. Convertir la posición de lat/lng a la celda H3 central (Resolución 8)
        String centerCell = h3RadarService.latLngToCell(userLocation);

        // 3. Calcular el anillo perimetral (k-ring) concéntrico según el radio solicitado
        List<String> radarCells = h3RadarService.calculateRadarRing(centerCell, request.getKRingRadius());

        // 4. Buscar en ráfaga dentro de Redis los restaurantes indexados en esas celdas
        List<String> rawRestaurantsJson = redisRadarRepository.getRestaurantsFromCells(radarCells);

        // 5. Deserializar los JSON strings a objetos DTO estructurados para Mapbox
        List<NearbyRestaurantDto> nearbyRestaurants = new ArrayList<>();
        for (String json : rawRestaurantsJson) {
            try {
                NearbyRestaurantDto dto = objectMapper.readValue(json, NearbyRestaurantDto.class);
                nearbyRestaurants.add(dto);
            } catch (Exception e) {
                System.err.println("[RadarService] Error de mapeo de JSON en caché: " + e.getMessage());
            }
        }

        return nearbyRestaurants;
    }
}