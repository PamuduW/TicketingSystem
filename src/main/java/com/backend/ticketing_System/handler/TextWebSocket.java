package com.backend.ticketing_System.handler;

import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriTemplate;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class TextWebSocket extends TextWebSocketHandler {
    private static final Logger logger = LoggerFactory.getLogger(TextWebSocket.class);
    private static final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        try {
            Map<String, String> pathVariables = new UriTemplate("/ws/{eventId}/{vendorId}")
                    .match(session.getUri().getPath());
            String eventId = pathVariables.get("eventId");
            String vendorId = pathVariables.get("vendorId");
            logger.info("Connection established for event: {} and vendor: {}", eventId, vendorId);
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
        String timestamp = LocalDateTime.now().format(formatter);
        String messageWithTimestamp = "---[" + timestamp + "]" + message;
        for (WebSocketSession session : sessions) {
            try {
                session.sendMessage(new TextMessage(messageWithTimestamp));
            } catch (IOException e) {
                logger.error("Error broadcasting message", e);
            }
        }
    }
}