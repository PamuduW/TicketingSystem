package com.backend.ticketing_System;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main class for the Ticketing System application.
 * This class contains the main method which is the entry point of the Spring Boot application.
 */
@SpringBootApplication
public class TicketingSystemApplication {

    /**
     * The main method which starts the Spring Boot application.
     *
     * @param args command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        SpringApplication.run(TicketingSystemApplication.class, args);
    }

}