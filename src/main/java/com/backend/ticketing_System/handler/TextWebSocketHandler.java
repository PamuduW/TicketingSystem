package com.backend.ticketing_System.handler;

import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * WebSocket handler for processing text messages.
 */
public class TextWebSocketHandler extends org.springframework.web.socket.handler.TextWebSocketHandler {
    private static final Logger logger = LoggerFactory.getLogger(TextWebSocketHandler.class);
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
        broadcast(payload);
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
     * Broadcasts a message to all connected sessions.
     *
     * @param message the message to broadcast.
     */
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