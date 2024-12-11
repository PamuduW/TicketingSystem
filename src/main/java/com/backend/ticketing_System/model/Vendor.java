package com.backend.ticketing_System.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Represents a vendor in the system.
 */
@Document(collection = "Vendors")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Vendor {
    /**
     * The unique identifier for the vendor.
     */
    @Id
    private String vendorId;

    /**
     * The username of the vendor.
     */
    private String username;

    /**
     * The password of the vendor.
     */
    private String pass;
}