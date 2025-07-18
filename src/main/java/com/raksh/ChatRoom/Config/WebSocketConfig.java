package com.raksh.ChatRoom.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig  implements WebSocketMessageBrokerConfigurer{
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")//stomp endpoints where user will connect
                .setAllowedOrigins("http://localhost:8080")// allow websocket origins from this origin only
                .withSockJS();//add fallback options for browsers that doesnt support websockets

    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");//clients will subscribe to topics like /topic/message to recieve message
        registry.setApplicationDestinationPrefixes("/app");//clients  will send messages to /app/sendMessage

    }
}
