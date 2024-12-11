package com.backend.ticketing_System.repository;

import com.backend.ticketing_System.model.Vendor;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * Repository interface for Vendor entities.
 */
public interface VendorRepository extends MongoRepository<Vendor, String> {
    /**
     * Finds a vendor by their username.
     *
     * @param vendorUsername the username of the vendor.
     * @return an Optional containing the found vendor, or empty if no vendor was found.
     */
    Optional<Vendor> findByUsername(String vendorUsername);
}