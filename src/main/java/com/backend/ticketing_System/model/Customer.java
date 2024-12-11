package com.backend.ticketing_System.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Represents a customer in the system.
 */
@Document(collection = "Customers")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Customer {
    /**
     * The unique identifier for the customer.
     */
    @Id
    private String customerId;

    /**
     * The username of the customer.
     */
    private String username;

    /**
     * The password of the customer.
     */
    private String pass;
}