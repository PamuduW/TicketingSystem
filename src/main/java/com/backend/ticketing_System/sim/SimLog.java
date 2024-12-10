package com.backend.ticketing_System.sim;

import com.backend.ticketing_System.handler.IntegerWebSocketHandler;
import com.backend.ticketing_System.handler.TextWebSocketHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SimLog {
    public static String log = "";
    public static List<List<Integer>> logInt = new ArrayList<>();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void logging(String logM) {
        String timestamp = LocalDateTime.now().format(formatter);
        String str = String.format("---[%s]%s", timestamp, logM);
        TextWebSocketHandler.broadcast(str);
        log += str + "\n";
    }

    public static void loggingWithNums(String logM, int currentTickets, int allSoldTickets, boolean finalTransaction) {
        logging(logM);
        if (finalTransaction) currentTickets = 0;
        List<Integer> nums = List.of(currentTickets, allSoldTickets);
        IntegerWebSocketHandler.broadcast(currentTickets, allSoldTickets);
        logInt.add(nums);
    }
}