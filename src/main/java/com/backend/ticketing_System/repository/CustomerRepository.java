package com.backend.ticketing_System.repository;

import com.backend.ticketing_System.model.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CustomerRepository extends MongoRepository<Customer, String> {
    Optional<Customer> findByUsername(String customerUsername);
}