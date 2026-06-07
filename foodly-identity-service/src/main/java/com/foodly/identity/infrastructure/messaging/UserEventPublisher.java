package com.foodly.identity.infrastructure.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.foodly.identity.application.dto.UserCreatedEventDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.jms.JMSContext;
import jakarta.jms.Queue;
import jakarta.jms.TextMessage;

@ApplicationScoped
public class UserEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(UserEventPublisher.class);

    public static final String EVENT_TYPE_USER_CREATED  = "USER_CREATED";
    public static final String EVENT_TYPE_USER_UPDATED  = "USER_UPDATED";

    @Resource(lookup = "java:/jms/queue/IdentityUsersQueue")
    private Queue queue;

    @Inject
    private JMSContext jmsContext;

    private final ObjectMapper objectMapper;

    public UserEventPublisher() {
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public void publishUserCreatedEvent(UserCreatedEventDto event) {
        publishEvent(event, EVENT_TYPE_USER_CREATED);
    }

    public void publishUserUpdatedEvent(UserCreatedEventDto event) {
        publishEvent(event, EVENT_TYPE_USER_UPDATED);
    }

    private void publishEvent(UserCreatedEventDto event, String eventType) {
        try {
            String jsonPayload = objectMapper.writeValueAsString(event);

            log.info("[UserEventPublisher] Publicando evento '{}' para userId={}", eventType, event.getUserId());
            log.debug("[UserEventPublisher] Payload JSON: {}", jsonPayload);

            TextMessage message = jmsContext.createTextMessage(jsonPayload);
            message.setStringProperty("eventType", eventType);
            message.setStringProperty("sourceService", "foodly-identity-service");
            message.setStringProperty("userId", event.getUserId());

            jmsContext.createProducer().send(queue, message);

            log.info("[UserEventPublisher] Evento '{}' enviado de forma reactiva a WildFly.", eventType);

        } catch (JsonProcessingException e) {
            log.error("[UserEventPublisher] Error al serializar el evento a JSON: {}", e.getMessage());
            throw new RuntimeException("Error al serializar evento de usuario a JSON", e);
        } catch (jakarta.jms.JMSException e) {
            log.error("[UserEventPublisher] Error crítico en el broker de mensajería ActiveMQ: {}", e.getMessage());
            throw new RuntimeException("Error al interactuar con la cola ActiveMQ", e);
        }
    }
}