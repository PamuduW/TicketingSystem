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

@RestController
@RequestMapping("/api")
public class EventController {
    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/events")
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }

    @GetMapping("/event/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable String id) {
        return eventService.getEventById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/event")
    public ResponseEntity<?> createEvent(@RequestBody Event event) {
        try {
            return new ResponseEntity<>(eventService.createEvent(event), HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/events/owner/{ownerId}")
    public List<Event> getAllEventsByOwnerId(@PathVariable String ownerId) {
        return eventService.getAllByOwnerId(ownerId);
    }

    @GetMapping("/events/vendor/{vendorId}")
    public List<Event> getAllEventsByVendorId(@PathVariable String vendorId) {
        return eventService.getAllByVendorId(vendorId);
    }

    @PutMapping("/event/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable String id, @RequestBody Event eventDetails) {
        try {
            return ResponseEntity.ok(eventService.updateEvent(id, eventDetails));
        } catch (IOException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @PutMapping("/event/{id}/vendors")
    public ResponseEntity<?> updateEventVendors(@PathVariable String id, @RequestBody List<String> vendors) {
        try {
            return ResponseEntity.ok(eventService.updateEventVendors(id, vendors));
        } catch (IOException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @PutMapping("/event/{id}/addTickets")
    public ResponseEntity<?> addTickets(@PathVariable String id, @RequestParam int ticketCount) {
        try {
            return ResponseEntity.ok(eventService.addTickets(id, ticketCount));
        } catch (IOException e) {
            HttpStatus status = e.getMessage().equals("Exceeded the total ticket limit") ? HttpStatus.BAD_REQUEST : HttpStatus.CONFLICT;
            return new ResponseEntity<>(e.getMessage(), status);
        }
    }

    @PutMapping("/event/{id}/buyTickets")
    public ResponseEntity<?> buyTickets(@PathVariable String id, @RequestParam int ticketCount, @RequestParam String customerId) {
        try {
            eventService.buyTickets(id, ticketCount, customerId);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/event/tickets/{customerId}")
    public Dictionary<String, List<String>> getTicketsByCustomerId(@PathVariable String customerId) {
        return eventService.getTicketsByCustomerId(customerId);
    }

    @DeleteMapping("/event/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable String id) {
        try {
            eventService.deleteEvent(id);
            return ResponseEntity.noContent().build();
        } catch (IOException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/event/{id}/startSim")
    public ResponseEntity<?> startSimulation(@PathVariable String id, @RequestBody List<Integer> data) {
        try {
            eventService.startSimulation(id, data.get(0), data.get(1), data.get(2), data.get(3), data.get(4), data.get(5));
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/event/{id}/stopSim")
    public ResponseEntity<?> stopSimulation(@PathVariable String id) {
        try {
            eventService.stopSimulation(id);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/event/{id}/saveLogs")
    public ResponseEntity<?> saveLogs(@PathVariable String id) {
        try {
            eventService.saveLogs(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/event/{id}/getConfig")
    public ResponseEntity<?> getConfig(@PathVariable String id) {
        try {
            return ResponseEntity.ok(eventService.getConfig(id));
        } catch (IOException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }
}