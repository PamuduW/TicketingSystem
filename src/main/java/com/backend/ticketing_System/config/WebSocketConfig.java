package com.backend.ticketing_System.config;

import com.backend.ticketing_System.handler.IntegerWebSocketHandler;
import com.backend.ticketing_System.handler.TextWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new TextWebSocketHandler(), "/ws/text").setAllowedOrigins("*");
        registry.addHandler(new IntegerWebSocketHandler(), "/ws/integers").setAllowedOrigins("*");
    }
}