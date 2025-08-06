package com.equipment.ms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // Enables WebSocket message handling, backed by a message broker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Configures the message broker.
     *
     * @param config The MessageBrokerRegistry to configure.
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable a simple in-memory message broker.
        // Messages with destinations prefixed with "/topic" will be routed to the broker.
        config.enableSimpleBroker("/topic");

        // Set the application destination prefix.
        // Messages from clients with destinations prefixed with "/app" will be routed
        // to @MessageMapping annotated methods in @Controller classes.
        config.setApplicationDestinationPrefixes("/app");
    }

    /**
     * Registers STOMP endpoints that clients will use to connect to the WebSocket server.
     *
     * @param registry The StompEndpointRegistry to register endpoints.
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register the "/ws-events" endpoint.
        // Clients will connect to this URL for WebSocket communication.
        // .withSockJS() enables SockJS fallback options for browsers that don't support WebSockets.
        // .setAllowedOrigins("*") allows connections from any origin (be cautious in production).
        registry.addEndpoint("/ws-events")
                .setAllowedOriginPatterns("*", "null") // Consider restricting this to specific origins in production. null for when you assess the html directly from the disk (like double click on the file)
                //.setAllowedOrigins("http://localhost:8080") // place the web origins
                .withSockJS();
    }
}
