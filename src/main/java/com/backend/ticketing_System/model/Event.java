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

@Document(collection = "Events")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    @Id
    private String eventId;
    private String name;
    private String ownerId;
    private String desc;
    private int totalTickets;
    private int maxCapacity;
    private int currentTickets = 0;
    private int issuedTickets = 0;
    private int totalTicketsAdded = 0;
    private List<Ticket> tickets = new ArrayList<>();
    private Set<String> vendors = new HashSet<>();
    private List<String> Logs = new ArrayList<>();
    private List<List<List<Integer>>> IntLogs = new ArrayList<>();
    private List<Integer> config = new ArrayList<>();
}