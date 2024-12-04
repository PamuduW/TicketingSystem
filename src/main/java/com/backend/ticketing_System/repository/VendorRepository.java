package com.backend.ticketing_System.repository;

import com.backend.ticketing_System.model.Vendor;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface VendorRepository extends MongoRepository<Vendor, String> {
    Optional<Vendor> findByUsername(String vendorUsername);
}