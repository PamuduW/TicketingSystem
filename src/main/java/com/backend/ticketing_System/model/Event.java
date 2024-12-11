package com.backend.ticketing_System.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents an event in the ticketing system.
 */
@Document(collection = "Events")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    /**
     * The unique identifier for the event.
     */
    @Id
    private String eventId;

    /**
     * The name of the event.
     */
    private String name;

    /**
     * The ID of the owner of the event.
     */
    private String ownerId;

    /**
     * A description of the event.
     */
    private String desc;

    /**
     * The total number of tickets available for the event.
     */
    private int totalTickets;

    /**
     * The maximum capacity of the event.
     */
    private int maxCapacity;

    /**
     * The current number of tickets sold for the event.
     */
    private int currentTickets = 0;

    /**
     * The total number of tickets issued for the event.
     */
    private int issuedTickets = 0;

    /**
     * The total number of tickets added to the event.
     */
    private int totalTicketsAdded = 0;

    /**
     * The list of tickets for the event.
     */
    private List<Ticket> tickets = new ArrayList<>();

    /**
     * The set of vendor IDs associated with the event.
     */
    private Set<String> vendors = new HashSet<>();

    /**
     * The list of logs for the event.
     */
    private List<String> Logs = new ArrayList<>();

    /**
     * The list of integer logs for the event.
     */
    private List<List<List<Integer>>> IntLogs = new ArrayList<>();

    /**
     * The configuration settings for the event.
     */
    private List<Integer> config = new ArrayList<>();
}