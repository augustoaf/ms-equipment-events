package com.equipment.ms.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Inject values from application.properties
    @Value("${rabbitmq.queue.name}")
    private String queueName;

    @Value("${rabbitmq.topic.exchange.name}")
    private String topicExchangeName;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    /**
     * Defines a durable queue in RabbitMQ.
     * A durable queue survives broker restarts.
     *
     * @return The configured Queue bean.
     */
    @Bean
    public Queue queue() {
        return new Queue(queueName, true); // true means durable
    }

    /**
     * Defines a Topic Exchange in RabbitMQ.
     * Topic exchanges route messages to queues based on a routing key pattern.
     *
     * @return The configured TopicExchange bean.
     */
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(topicExchangeName);
    }

    /**
     * Binds the queue to the topic exchange with a specific routing key.
     * Messages sent to the exchange with a routing key matching this pattern
     * will be delivered to this queue.
     *
     * @param queue The Queue bean.
     * @param exchange The TopicExchange bean.
     * @return The configured Binding bean.
     */
    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(routingKey);
    }

    /**
     * Configures a Jackson2JsonMessageConverter for converting Java objects to JSON
     * and vice versa when sending/receiving messages from RabbitMQ.
     * This is crucial for sending complex objects as messages.
     *
     * @return The configured MessageConverter bean.
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * Configures the RabbitTemplate, which is used to send and receive messages.
     * It's set up to use the custom JSON message converter.
     *
     * @param connectionFactory The RabbitMQ connection factory.
     * @return The configured RabbitTemplate bean.
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
