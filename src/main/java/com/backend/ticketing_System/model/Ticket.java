package com.backend.ticketing_System.model;

import lombok.*;
import org.springframework.data.annotation.Id;

/**
 * Represents a ticket in the system.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class Ticket {
    /**
     * The unique identifier for the ticket.
     */
    @Id
    @NonNull
    private String ticketId;

    /**
     * The unique identifier for the customer associated with the ticket.
     */
    private String customerId;
}