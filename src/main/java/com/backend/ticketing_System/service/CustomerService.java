package com.backend.ticketing_System.service;

import com.backend.ticketing_System.model.Customer;
import com.backend.ticketing_System.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class CustomerService {
    private final CustomerRepository customerRepo;
    private final ReentrantLock lock = new ReentrantLock(true);

    @Autowired
    public CustomerService(CustomerRepository customerRepo) {
        this.customerRepo = customerRepo;
    }

    public List<Customer> getAllCustomers() {
        return customerRepo.findAll();
    }

    public Optional<Customer> getCustomerByUsername(String customerUsername) {
        return customerRepo.findByUsername(customerUsername);
    }

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

    public Customer updateCustomer(String CustomerId, Customer CustomerDetails) throws IOException {
        lock.lock();
        try {
            Optional<Customer> optionalCustomer = customerRepo.findById(CustomerId);
            if (optionalCustomer.isPresent()) {
                Customer Customer = optionalCustomer.get();
                Customer.setUsername(CustomerDetails.getUsername());
                Customer.setPass(CustomerDetails.getPass());
                return customerRepo.save(Customer);
            } else throw new IOException("Customer not found with id " + CustomerId);
        } finally {
            lock.unlock();
        }
    }

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