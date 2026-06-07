package com.foodly.community.infrastructure.messaging.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.inject.Inject;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;
import java.util.Map;

@MessageDriven(name = "OrderCompletedListener", activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "java:/jms/queue/orders.completed.queue"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Queue")
})
public class OrderCompletedListener implements MessageListener {

    private static final Logger log = LoggerFactory.getLogger(OrderCompletedListener.class);

    private final ObjectMapper objectMapper;

    public OrderCompletedListener() {
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                String messageJson = textMessage.getText();

                Map<String, Object> eventData = objectMapper.readValue(messageJson, Map.class);
                log.info("[OrderCompletedListener] Evento reactivo recibido en WildFly: {}", eventData);

                String customerUsername = (String) eventData.get("customerUsername");
                String huariqueId = (String) eventData.get("huariqueId");

                if (customerUsername != null && huariqueId != null) {
                    log.info("[OrderCompletedListener] Habilitando reseñas verificadas para cliente '{}' en el huarique '{}'",
                            customerUsername, huariqueId);
                }
            } else {
                log.warn("[OrderCompletedListener] Se recibió un tipo de mensaje inesperado en la cola");
            }

        } catch (jakarta.jms.JMSException e) {
            log.error("[OrderCompletedListener] Error crítico de comunicación con el broker ActiveMQ integrado", e);
        } catch (Exception e) {
            log.error("[OrderCompletedListener] Error inesperado al procesar el payload JSON del evento", e);
        }
    }
}