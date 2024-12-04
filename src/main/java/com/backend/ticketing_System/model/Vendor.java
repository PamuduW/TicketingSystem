package com.backend.ticketing_System.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Vendors")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Vendor {
    @Id
    private String vendorId;
    private String username;
    private String pass;
}