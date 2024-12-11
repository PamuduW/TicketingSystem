package com.backend.ticketing_System.repository;

import com.backend.ticketing_System.model.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * Repository interface for Customer entities.
 */
public interface CustomerRepository extends MongoRepository<Customer, String> {
    /**
     * Finds a customer by their username.
     *
     * @param customerUsername the username of the customer.
     * @return an Optional containing the found customer, or empty if no customer was found.
     */
    Optional<Customer> findByUsername(String customerUsername);
}