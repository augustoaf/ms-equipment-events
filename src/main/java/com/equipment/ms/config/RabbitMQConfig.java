package com.equipment.ms.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.AnonymousQueue;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;

@Configuration
public class RabbitMQConfig {

    // Inject values from application.properties
    @Value("${rabbitmq.queue.name}")
    private String queueName;

    @Value("${rabbitmq.exchange.name}")
    private String fanoutExchangeName;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    // Declare a custom 'Listener Factory' Bean, then refer to the @RabbitListener annotation, 
    // otherwise Spring Boot will inject a default Listener Factory
    @Bean(name = "myListenerFactory")
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            SimpleRabbitListenerContainerFactoryConfigurer configurer,
            ConnectionFactory connectionFactory) {
        
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        
        // Set acknowledgment mode 
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);

        // enable the listener to process messages in parallel (using multiple threads) 
        // if you need ordering, use a single thread, then do not need to set these parameters
        factory.setConcurrentConsumers(2);
        factory.setMaxConcurrentConsumers(3);
        
        return factory;
    }

    /**
     * Defines a queue in RabbitMQ.
     *
     * @return The configured Queue bean.
     */
    @Bean
    public Queue queue() {
        //return new Queue(queueName, true); // queue with a harcoded name and durable
        return new AnonymousQueue(); // queue with a random queue name, non-durable and auto-deleted when this service goes down
    }

    /**
     * Defines a Fanout Exchange in RabbitMQ.
     * Fanout exchanges route messages to all queues binded.
     *
     * @return The configured FanoutExchange bean.
     */
    @Bean
    public FanoutExchange exchange() {
        return new FanoutExchange(fanoutExchangeName, true, false); //durable fanout exchange
    }

    /**
     * Binds the queue to the fanout exchange.
     * Messages sent to the exchange will be replicated to all 
     * AnonymousQueue created (distinct queues created for each microservice instance).
     *
     * @param queue The Queue bean.
     * @param exchange The FanoutExchange bean.
     * @return The configured Binding bean.
     */
    @Bean
    public Binding binding(Queue queue, FanoutExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange);
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

    //NOTE: Bean currently not in use for this service.
    /**
     * Configures the RabbitTemplate, which is used to send messages. 
     * It's set up to use the custom JSON message converter.
     * 
     * @param connectionFactory The RabbitMQ connection factory.
     * @param jsonMessageConverter The MessageConverter Bean
     * @return The configured RabbitTemplate bean.
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter);
        return rabbitTemplate;
    }
}
