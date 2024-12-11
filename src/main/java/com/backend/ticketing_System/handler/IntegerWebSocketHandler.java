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

/**
 * WebSocket handler for processing integer messages.
 */
public class IntegerWebSocketHandler extends TextWebSocketHandler {
    private static final Logger logger = LoggerFactory.getLogger(IntegerWebSocketHandler.class);
    private static final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    /**
     * Called after a new WebSocket connection is established.
     *
     * @param session the WebSocket session.
     */
    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        sessions.add(session);
    }

    /**
     * Handles incoming text messages.
     *
     * @param session the WebSocket session.
     * @param message the text message.
     */
    @Override
    public void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) {
        String payload = message.getPayload();
        logger.info("Received message: {}", payload);
        String[] parts = payload.split(",");
        if (parts.length == 2) {
            try {
                int int1 = Integer.parseInt(parts[0].trim());
                int int2 = Integer.parseInt(parts[1].trim());
                broadcast(int1, int2);
            } catch (NumberFormatException e) {
                logger.error("Invalid number format", e);
            }
        } else {
            logger.error("Invalid message format");
        }
    }

    /**
     * Called after a WebSocket connection is closed.
     *
     * @param session the WebSocket session.
     * @param status the close status.
     */
    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        sessions.remove(session);
    }

    /**
     * Broadcasts a message containing two integers to all connected sessions.
     *
     * @param int1 the first integer.
     * @param int2 the second integer.
     */
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