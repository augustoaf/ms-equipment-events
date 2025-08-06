package com.equipment.ms.listener;

import com.equipment.ms.model.EquipmentEvent;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class RabbitMQEventListener {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQEventListener.class);

    // SimpMessagingTemplate is used to send messages to WebSocket clients
    private final SimpMessagingTemplate messagingTemplate;
    private final Queue queue; // Inject the Queue bean

    @Autowired
    public RabbitMQEventListener(SimpMessagingTemplate messagingTemplate, Queue queue) {
        this.messagingTemplate = messagingTemplate;
        this.queue = queue; // Assign the injected Queue
    }

    /**
     * This method listens to the RabbitMQ queue defined by the Spring Bean in RabbitMQConfig class "#{queue.name}".
     * When a message (Event object) is received, it logs the event and then
     * broadcasts it to all WebSocket clients subscribed to "/topic/events".
     *
     * @param event The Event object received from RabbitMQ.
     */
    @RabbitListener(queues = "#{queue.name}")
    public void receiveEvent(EquipmentEvent event) {
        logger.info("Received event from RabbitMQ: {}", event.toString());

        // Broadcast the received event to WebSocket clients
        // The destination "/topic/events" corresponds to the topic clients subscribe to.
        messagingTemplate.convertAndSend("/topic/events", event);
        logger.info("Broadcasted event to WebSocket clients: {}", event);
    }
}
