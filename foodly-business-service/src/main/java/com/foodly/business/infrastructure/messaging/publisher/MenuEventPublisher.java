package com.foodly.business.infrastructure.messaging.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.foodly.business.application.dto.MenuUpdatedEventDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.jms.JMSContext;
import jakarta.jms.Queue;
import jakarta.jms.TextMessage;

@ApplicationScoped
public class MenuEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(MenuEventPublisher.class);
    private static final String QUEUE_NAME = "java:/jms/queue/BusinessMenusQueue";

    @Resource(lookup = QUEUE_NAME)
    private Queue queue;

    @Inject
    private JMSContext jmsContext;

    private final ObjectMapper objectMapper;

    public MenuEventPublisher() {
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public void publishMenuUpdatedEvent(MenuUpdatedEventDto event) {
        try {
            String jsonPayload = objectMapper.writeValueAsString(event);

            log.info("[MenuEventPublisher] Publicando evento de menú actualizado para huariqueId={}", event.getHuariqueId());

            TextMessage message = jmsContext.createTextMessage(jsonPayload);
            message.setStringProperty("eventType", "MENU_UPDATED");
            message.setStringProperty("sourceService", "foodly-business-service");

            jmsContext.createProducer().send(queue, message);

            log.info("[MenuEventPublisher] Evento enviado exitosamente a ActiveMQ.");

        } catch (JsonProcessingException e) {
            log.error("[MenuEventPublisher] Error al serializar el evento a JSON: {}", e.getMessage());
            throw new RuntimeException("Error de serialización JSON", e);
        } catch (jakarta.jms.JMSException e) {
            log.error("[MenuEventPublisher] Error crítico en el broker ActiveMQ: {}", e.getMessage());
            throw new RuntimeException("Error en la cola de mensajería", e);
        }
    }
}