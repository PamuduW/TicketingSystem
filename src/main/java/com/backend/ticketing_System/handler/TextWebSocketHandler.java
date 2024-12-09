package com.backend.ticketing_System.handler;

import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

public class TextWebSocketHandler extends org.springframework.web.socket.handler.TextWebSocketHandler {
    private static final Logger logger = LoggerFactory.getLogger(TextWebSocketHandler.class);
    private static final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        try {
            sessions.add(session);
        } catch (Exception e) {
            logger.error("Error adding session", e);
        }
    }

    @Override
    public void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) {
        try {
            String payload = message.getPayload();
            logger.info("Received message: {}", payload);
            broadcast(payload);
        } catch (Exception e) {
            logger.error("Error handling text message", e);
        }
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        try {
            sessions.remove(session);
        } catch (Exception e) {
            logger.error("Error removing session", e);
        }
    }

    public static void broadcast(String message) {
        for (WebSocketSession session : sessions) {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                logger.error("Error broadcasting message", e);
            }
        }
    }
}