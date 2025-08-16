package com.equipment.ms.listener;

import com.equipment.ms.model.EquipmentEvent;
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

    @Autowired
    public RabbitMQEventListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * This method listens to the Queue defined and created in RabbitMQConfig class
     * When a message (EquipmentEvent object) is received, it logs the event and then
     * broadcasts it to all WebSocket clients connected to "/topic/events" endpoint.
     *
     * @param event The EquipmentEvent object received from RabbitMQ.
     * 
     * Notes:
     * About the @RabbitListener annotation, it creates a listener (which consume from all specified queues) and 
     * the class method referenced by this annotation will receive the object consumed from the queue(s).
     * About "#{queue.name}", this instruction refer to the Sprint Context looking for a Bean named 'queue', 
     * then get its name to give as an input to the RabbitListener queues property.
     * The 'containerFactory = "myListenerFactory"' indicates to use a Bean named myListenerFactory, 
     * if that Bean wasn't declared, SpringBoot will give a defautl factory, then this parameter is not necessary
     */
    @RabbitListener(queues = "#{queue.name}", containerFactory = "myListenerFactory")
    public void receiveEvent(EquipmentEvent event) {
        logger.info("Received event from RabbitMQ: id {}, timestamp {}; from thread {}", event.getEquipmentId(), event.getTimestamp(), Thread.currentThread().getName());

        // Broadcast the received event to WebSocket clients
        // The destination "/topic/events" corresponds to the topic clients subscribe to.
        messagingTemplate.convertAndSend("/topic/events", event);
        logger.info("Broadcasted event to WebSocket clients: {}", event);
    }
}
