package com.backend.ticketing_System.service;

import com.backend.ticketing_System.model.Customer;
import com.backend.ticketing_System.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Service class for managing customers.
 */
@Service
public class CustomerService {
    private final CustomerRepository customerRepo;
    private final ReentrantLock lock = new ReentrantLock(true);

    /**
     * Constructs a new CustomerService with the given CustomerRepository.
     *
     * @param customerRepo the repository for customer data.
     */
    @Autowired
    public CustomerService(CustomerRepository customerRepo) {
        this.customerRepo = customerRepo;
    }

    /**
     * Retrieves all customers.
     *
     * @return a list of all customers.
     */
    public List<Customer> getAllCustomers() {
        return customerRepo.findAll();
    }

    /**
     * Retrieves a customer by their username.
     *
     * @param customerUsername the username of the customer.
     * @return an Optional containing the found customer, or empty if no customer was found.
     */
    public Optional<Customer> getCustomerByUsername(String customerUsername) {
        return customerRepo.findByUsername(customerUsername);
    }

    /**
     * Creates a new customer.
     *
     * @param customer the customer to create.
     * @return the created customer.
     * @throws IOException if a customer with the same username already exists.
     */
    public Customer createCustomer(Customer customer) throws IOException {
        lock.lock();
        try {
            if (customerRepo.findByUsername(customer.getUsername()).isPresent())
                throw new IOException("Customer with the same username already exists");
            return customerRepo.save(customer);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Updates an existing customer.
     *
     * @param customerId the ID of the customer to update.
     * @param customerDetails the new details for the customer.
     * @return the updated customer.
     * @throws IOException if the customer is not found.
     */
    public Customer updateCustomer(String customerId, Customer customerDetails) throws IOException {
        lock.lock();
        try {
            return customerRepo.findById(customerId)
                    .map(existingCustomer -> {
                        existingCustomer.setUsername(customerDetails.getUsername());
                        existingCustomer.setPass(customerDetails.getPass());
                        return customerRepo.save(existingCustomer);
                    })
                    .orElseThrow(() -> new IOException("Customer not found with id " + customerId));
        } finally {
            lock.unlock();
        }
    }

    /**
     * Deletes a customer.
     *
     * @param customerId the ID of the customer to delete.
     * @throws IOException if the customer is not found.
     */
    public void deleteCustomer(String customerId) throws IOException {
        lock.lock();
        try {
            if (customerRepo.findById(customerId).isEmpty())
                throw new IOException("this Customer does not exists");
            customerRepo.deleteById(customerId);
        } finally {
            lock.unlock();
        }
    }
}