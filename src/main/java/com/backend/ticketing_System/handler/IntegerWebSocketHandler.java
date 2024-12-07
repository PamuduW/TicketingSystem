package com.backend.ticketing_System.handler;

import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

public class    IntegerWebSocketHandler extends TextWebSocketHandler {
    private static final Logger logger = LoggerFactory.getLogger(IntegerWebSocketHandler.class);
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
            // Assuming the payload contains two integers separated by a comma
            String[] parts = payload.split(",");
            if (parts.length == 2) {
                int int1 = Integer.parseInt(parts[0].trim());
                int int2 = Integer.parseInt(parts[1].trim());
                broadcast(int1, int2);
            } else {
                logger.error("Invalid message format");
            }
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

    public static void broadcast(int int1, int int2) {
        String message = int1 + "," + int2;
        for (WebSocketSession session : sessions) {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                logger.error("Error broadcasting message", e);
            }
        }
    }
}