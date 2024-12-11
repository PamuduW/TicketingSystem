package com.backend.ticketing_System.controller;

import com.backend.ticketing_System.model.Customer;
import com.backend.ticketing_System.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing customers.
 */
@RestController
@RequestMapping("/api")
public class CustomerController {
    private final CustomerService customerService;

    /**
     * Constructor for CustomerController.
     *
     * @param customerService the service to manage customers.
     */
    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    /**
     * Get all customers.
     *
     * @return a list of all customers.
     */
    @GetMapping("/customers")
    public List<Customer> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    /**
     * Get a customer by username and password.
     *
     * @param username the username of the customer.
     * @param pass the password of the customer.
     * @return the customer if found and password matches, otherwise appropriate HTTP status.
     */
    @GetMapping("/customer")
    public ResponseEntity<Customer> getCustomerById(@RequestParam String username, @RequestParam String pass) {
        Optional<Customer> optionalCustomer = customerService.getCustomerByUsername(username);
        if (optionalCustomer.isPresent()){
            if (optionalCustomer.get().getPass().equals(pass))
                return ResponseEntity.accepted().body(optionalCustomer.get());
            else return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Create a new customer.
     *
     * @param customer the customer to create.
     * @return the created customer or an error message if creation fails.
     */
    @PostMapping("/customer")
    public ResponseEntity<?> createCustomer(@RequestBody Customer customer) {
        try {
            return new ResponseEntity<>(customerService.createCustomer(customer), HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    /**
     * Update an existing customer.
     *
     * @param id the ID of the customer to update.
     * @param customerDetails the new details of the customer.
     * @return the updated customer or an error message if update fails.
     */
    @PutMapping("/customer/{id}")
    public ResponseEntity<?> updateCustomer(@PathVariable String id, @RequestBody Customer customerDetails) {
        try {
            Customer updatedCustomer = customerService.updateCustomer(id, customerDetails);
            return ResponseEntity.ok(updatedCustomer);
        } catch (IOException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    /**
     * Delete a customer.
     *
     * @param id the ID of the customer to delete.
     * @return no content if deletion is successful, otherwise an error message.
     */
    @DeleteMapping("/customer/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable String id) {
        try {
            customerService.deleteCustomer(id);
            return ResponseEntity.noContent().build();
        } catch (IOException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }
}