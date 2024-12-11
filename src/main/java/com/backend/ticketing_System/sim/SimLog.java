package com.backend.ticketing_System.sim;

import com.backend.ticketing_System.handler.IntegerWebSocketHandler;
import com.backend.ticketing_System.handler.TextWebSocketHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for logging simulation events and broadcasting messages.
 */
public class SimLog {
    public static String log = "";
    public static List<List<Integer>> logInt = new ArrayList<>();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Logs a message with a timestamp and broadcasts it to all connected WebSocket clients.
     *
     * @param logM the message to log and broadcast.
     */
    public static void logging(String logM) {
        String timestamp = LocalDateTime.now().format(formatter);
        String str = String.format("---[%s]%s", timestamp, logM);
        TextWebSocketHandler.broadcast(str);
        log += str + "\n";
    }

    /**
     * Logs a message with a timestamp, broadcasts it to all connected WebSocket clients,
     * and broadcasts the current and total sold tickets to integer WebSocket clients.
     *
     * @param logM the message to log and broadcast.
     * @param currentTickets the current number of tickets.
     * @param allSoldTickets the total number of sold tickets.
     * @param finalTransaction whether this is the final transaction.
     */
    public static void loggingWithNums(String logM, int currentTickets, int allSoldTickets, boolean finalTransaction) {
        logging(logM);
        if (finalTransaction) currentTickets = 0;
        List<Integer> nums = List.of(currentTickets, allSoldTickets);
        IntegerWebSocketHandler.broadcast(currentTickets, allSoldTickets);
        logInt.add(nums);
    }
}