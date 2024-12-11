package com.backend.ticketing_System.controller;

import com.backend.ticketing_System.model.Event;
import com.backend.ticketing_System.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Dictionary;
import java.util.List;

/**
 * REST controller for managing events.
 */
@RestController
@RequestMapping("/api")
public class EventController {
    private final EventService eventService;

    /**
     * Constructor for EventController.
     *
     * @param eventService the event service
     */
    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * Get all events.
     *
     * @return a list of all events
     */
    @GetMapping("/events")
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }

    /**
     * Get an event by its ID.
     *
     * @param id the event ID
     * @return the event if found, otherwise a 404 response
     */
    @GetMapping("/event/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable String id) {
        return eventService.getEventById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Create a new event.
     *
     * @param event the event to create
     * @return the created event, or a conflict response if an error occurs
     */
    @PostMapping("/event")
    public ResponseEntity<?> createEvent(@RequestBody Event event) {
        try {
            return new ResponseEntity<>(eventService.createEvent(event), HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    /**
     * Get all events by owner ID.
     *
     * @param ownerId the owner ID
     * @return a list of events owned by the specified owner
     */
    @GetMapping("/events/owner/{ownerId}")
    public List<Event> getAllEventsByOwnerId(@PathVariable String ownerId) {
        return eventService.getAllByOwnerId(ownerId);
    }

    /**
     * Get all events by vendor ID.
     *
     * @param vendorId the vendor ID
     * @return a list of events associated with the specified vendor
     */
    @GetMapping("/events/vendor/{vendorId}")
    public List<Event> getAllEventsByVendorId(@PathVariable String vendorId) {
        return eventService.getAllByVendorId(vendorId);
    }

    /**
     * Update an event.
     *
     * @param id the event ID
     * @param eventDetails the updated event details
     * @return the updated event, or a conflict response if an error occurs
     */
    @PutMapping("/event/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable String id, @RequestBody Event eventDetails) {
        try {
            return ResponseEntity.ok(eventService.updateEvent(id, eventDetails));
        } catch (IOException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    /**
     * Update the vendors of an event.
     *
     * @param id the event ID
     * @param vendors the list of vendor IDs
     * @return the updated event, or a conflict response if an error occurs
     */
    @PutMapping("/event/{id}/vendors")
    public ResponseEntity<?> updateEventVendors(@PathVariable String id, @RequestBody List<String> vendors) {
        try {
            return ResponseEntity.ok(eventService.updateEventVendors(id, vendors));
        } catch (IOException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    /**
     * Add tickets to an event.
     *
     * @param id the event ID
     * @param ticketCount the number of tickets to add
     * @return the updated event, or a conflict response if an error occurs
     */
    @PutMapping("/event/{id}/addTickets")
    public ResponseEntity<?> addTickets(@PathVariable String id, @RequestParam int ticketCount) {
        try {
            return ResponseEntity.ok(eventService.addTickets(id, ticketCount));
        } catch (IOException e) {
            HttpStatus status = e.getMessage().equals("Exceeded the total ticket limit") ? HttpStatus.BAD_REQUEST : HttpStatus.CONFLICT;
            return new ResponseEntity<>(e.getMessage(), status);
        }
    }

    /**
     * Buy tickets for an event.
     *
     * @param id the event ID
     * @param ticketCount the number of tickets to buy
     * @param customerId the customer ID
     * @return a response indicating the result of the operation
     */
    @PutMapping("/event/{id}/buyTickets")
    public ResponseEntity<?> buyTickets(@PathVariable String id, @RequestParam int ticketCount, @RequestParam String customerId) {
        try {
            eventService.buyTickets(id, ticketCount, customerId);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    /**
     * Get tickets by customer ID.
     *
     * @param customerId the customer ID
     * @return a dictionary of events and their associated tickets for the specified customer
     */
    @GetMapping("/event/tickets/{customerId}")
    public Dictionary<String, List<String>> getTicketsByCustomerId(@PathVariable String customerId) {
        return eventService.getTicketsByCustomerId(customerId);
    }

    /**
     * Delete an event.
     *
     * @param id the event ID
     * @return a response indicating the result of the operation
     */
    @DeleteMapping("/event/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable String id) {
        try {
            eventService.deleteEvent(id);
            return ResponseEntity.noContent().build();
        } catch (IOException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    /**
     * Start a simulation for an event.
     *
     * @param id the event ID
     * @param data the simulation parameters
     * @return a response indicating the result of the operation
     */
    @PostMapping("/event/{id}/startSim")
    public ResponseEntity<?> startSimulation(@PathVariable String id, @RequestBody List<Integer> data) {
        try {
            eventService.startSimulation(id, data.get(0), data.get(1), data.get(2), data.get(3), data.get(4), data.get(5));
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    /**
     * Stop a simulation for an event.
     *
     * @param id the event ID
     * @return a response indicating the result of the operation
     */
    @PostMapping("/event/{id}/stopSim")
    public ResponseEntity<?> stopSimulation(@PathVariable String id) {
        try {
            eventService.stopSimulation(id);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    /**
     * Save logs for an event.
     *
     * @param id the event ID
     * @return a response indicating the result of the operation
     */
    @PostMapping("/event/{id}/saveLogs")
    public ResponseEntity<?> saveLogs(@PathVariable String id) {
        try {
            eventService.saveLogs(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Get the configuration for an event.
     *
     * @param id the event ID
     * @return the event configuration, or a conflict response if an error occurs
     */
    @GetMapping("/event/{id}/getConfig")
    public ResponseEntity<?> getConfig(@PathVariable String id) {
        try {
            return ResponseEntity.ok(eventService.getConfig(id));
        } catch (IOException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }
}