package com.db.jogo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

    public static final String HTTP_LOCALHOST_4200 = "http://localhost:4200/";
    public static final String DEV_FRONTEND_RENDER = "https://dev-frontendlila.onrender.com/";

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/gameplay")
                .setAllowedOrigins(HTTP_LOCALHOST_4200, DEV_FRONTEND_RENDER)
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/game-app")
                .enableSimpleBroker("/gameplay");
    }
}
