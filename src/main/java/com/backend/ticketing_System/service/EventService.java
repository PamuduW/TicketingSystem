package com.backend.ticketing_System.service;

import com.backend.ticketing_System.model.Event;
import com.backend.ticketing_System.model.Ticket;
import com.backend.ticketing_System.repository.EventRepository;
import com.backend.ticketing_System.repository.VendorRepository;
import com.backend.ticketing_System.sim.Sim;
import com.backend.ticketing_System.sim.SimLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Service class for managing events.
 */
@Service
public class EventService {
    private final EventRepository eventRepo;
    private final VendorRepository vendorRepo;
    private final ReentrantLock lock = new ReentrantLock(true);

    /**
     * Constructor for EventService.
     *
     * @param eventRepo the event repository
     * @param vendorRepo the vendor repository
     */
    @Autowired
    public EventService(EventRepository eventRepo, VendorRepository vendorRepo) {
        this.eventRepo = eventRepo;
        this.vendorRepo = vendorRepo;
    }

    /**
     * Get all events.
     *
     * @return a list of all events
     */
    public List<Event> getAllEvents() {
        return eventRepo.findAll();
    }

    /**
     * Get an event by its ID.
     *
     * @param eventId the event ID
     * @return an optional containing the event if found, otherwise empty
     */
    public Optional<Event> getEventById(String eventId) {
        return eventRepo.findById(eventId);
    }

    /**
     * Create a new event.
     *
     * @param event the event to create
     * @return the created event
     * @throws IOException if an event with the same name already exists
     */
    public Event createEvent(Event event) throws IOException {
        lock.lock();
        try {
            if (eventRepo.findByName(event.getName()).isPresent())
                throw new IOException("Event with the same name already exists");
            event.getVendors().add(event.getOwnerId());
            return eventRepo.save(event);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Get all events by owner ID.
     *
     * @param ownerId the owner ID
     * @return a list of events owned by the specified owner
     */
    public List<Event> getAllByOwnerId(String ownerId) {
        return eventRepo.findAllByOwnerId(ownerId);
    }

    /**
     * Get all events by vendor ID.
     *
     * @param vendorId the vendor ID
     * @return a list of events associated with the specified vendor
     */
    public List<Event> getAllByVendorId(String vendorId) {
        List<Event> list = new ArrayList<>();
        for (Event event : eventRepo.findAll()) {
            if (event.getVendors().contains(vendorId)) {
                list.add(event);
            }
        }
        return list;
    }

    /**
     * Update an event.
     *
     * @param eventId the event ID
     * @param eventDetails the updated event details
     * @return the updated event
     * @throws IOException if the event is not found
     */
    public Event updateEvent(String eventId, Event eventDetails) throws IOException {
        lock.lock();
        try {
            return eventRepo.findById(eventId)
                    .map(event -> {
                        event.setName(eventDetails.getName());
                        event.setDesc(eventDetails.getDesc());
                        event.setTotalTickets(eventDetails.getTotalTickets());
                        event.setMaxCapacity(eventDetails.getMaxCapacity());
                        return eventRepo.save(event);
                    })
                    .orElseThrow(() -> new IOException("Event not found with id " + eventId));
        } finally {
            lock.unlock();
        }
    }

    /**
     * Update the vendors of an event.
     *
     * @param eventId the event ID
     * @param vendors the list of vendor IDs
     * @return the updated event
     * @throws IOException if the event is not found
     */
    public Event updateEventVendors(String eventId, List<String> vendors) throws IOException {
        lock.lock();
        try {
            return eventRepo.findById(eventId)
                    .map(event -> {
                        Set<String> set = new HashSet<>();
                        for (String vendorId : vendors) {
                            vendorRepo.findById(vendorId).ifPresent(vendor -> set.add(vendor.getVendorId()));
                        }
                        event.setVendors(set);
                        return eventRepo.save(event);
                    })
                    .orElseThrow(() -> new IOException("Event not found with id " + eventId));
        } finally {
            lock.unlock();
        }
    }

    /**
     * Add tickets to an event.
     *
     * @param eventId the event ID
     * @param ticketCount the number of tickets to add
     * @return the updated event
     * @throws IOException if the event is not found or if the ticket limits are exceeded
     */
    public Event addTickets(String eventId, int ticketCount) throws IOException {
        lock.lock();
        try {
            Optional<Event> optionalEvent = eventRepo.findById(eventId);
            if (optionalEvent.isPresent()) {
                Event event = optionalEvent.get();
                if (event.getCurrentTickets() + ticketCount <= event.getMaxCapacity()) {
                    if (event.getTotalTicketsAdded() + ticketCount <= event.getTotalTickets()) {
                        int holder;
                        for (int i = 0; i < ticketCount; i++) {
                            holder = event.getTotalTicketsAdded() + 1;
                            Ticket ticket = new Ticket(String.valueOf(holder));
                            event.setTotalTicketsAdded(holder);
                            event.getTickets().add(ticket);
                        }
                        holder = event.getCurrentTickets() + ticketCount;
                        event.setCurrentTickets(holder);
                        eventRepo.save(event);
                        return event;
                    } else throw new IOException("Exceeded the total ticket limit");
                } else throw new IOException("Exceeded the ticket pool limit");
            } else throw new IOException("Event not found with id " + eventId);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Buy tickets for an event.
     *
     * @param eventId the event ID
     * @param ticketCount the number of tickets to buy
     * @param customerId the customer ID
     * @throws IOException if the event is not found or if there are not enough tickets available
     */
    public void buyTickets(String eventId, int ticketCount, String customerId) throws IOException {
        lock.lock();
        try {
            Optional<Event> optionalEvent = eventRepo.findById(eventId);
            if (optionalEvent.isPresent()) {
                Event event = optionalEvent.get();
                if (event.getCurrentTickets() - ticketCount >= 0) {
                    for (int i = 0; i < ticketCount; i++) {
                        event.getTickets().get(event.getIssuedTickets()).setCustomerId(customerId);
                        int count = event.getIssuedTickets() + 1;
                        int count1 = event.getCurrentTickets() - 1;
                        event.setIssuedTickets(count);
                        event.setCurrentTickets(count1);
                    }
                    eventRepo.save(event);
                } else throw new IOException("There are no tickets available");
            } else throw new IOException("Event not found with id " + eventId);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Get tickets by customer ID.
     *
     * @param customerId the customer ID
     * @return a dictionary of events and their associated tickets for the specified customer
     */
    public Dictionary<String, List<String>> getTicketsByCustomerId(String customerId) {
        Dictionary<String, List<String>> dic = new Hashtable<>();
        for (Event event : eventRepo.findAll()) {
            List<String> list = new ArrayList<>();
            for (Ticket ticket : event.getTickets()) {
                if (customerId.equals(ticket.getCustomerId())) {
                    list.add(ticket.getTicketId());
                }
            }
            if (list.isEmpty()) continue;
            dic.put(event.getEventId(), list);
        }
        return dic;
    }

    /**
     * Delete an event.
     *
     * @param eventId the event ID
     * @throws IOException if the event is not found
     */
    public void deleteEvent(String eventId) throws IOException {
        lock.lock();
        try {
            if (eventRepo.findById(eventId).isEmpty())
                throw new IOException("Event not found with id " + eventId);
            eventRepo.deleteById(eventId);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Start a simulation for an event.
     *
     * @param eventID the event ID
     * @param vendorReleaseRate the vendor release rate
     * @param customerRetrievalRate the customer retrieval rate
     * @param noOfVendors the number of vendors
     * @param noOfCustomers the number of customers
     * @param noOfVIPCustomers the number of VIP customers
     * @param simSpeed the simulation speed
     * @throws IOException if the event is not found or if the simulation is already running
     */
    public void startSimulation(String eventID, int vendorReleaseRate, int customerRetrievalRate, int noOfVendors, int noOfCustomers, int noOfVIPCustomers, int simSpeed) throws IOException {
        lock.lock();
        try {
            Event event = eventRepo.findById(eventID).orElseThrow(() -> new IOException("Event not found with id " + eventID));
            if (!Sim.startSimulation(event.getTotalTickets(), vendorReleaseRate, customerRetrievalRate, event.getMaxCapacity(), noOfVendors, noOfCustomers, noOfVIPCustomers, simSpeed, eventID)) {
                throw new IOException("Simulation is already running.");
            }
            event.setConfig(Arrays.asList(vendorReleaseRate, customerRetrievalRate, noOfVendors, noOfCustomers, noOfVIPCustomers, simSpeed));
            eventRepo.save(event);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Stop a simulation for an event.
     *
     * @param id the event ID
     * @throws IOException if the event is not found or if the simulation is not running
     */
    public void stopSimulation(String id) throws IOException {
        lock.lock();
        try {
            if (eventRepo.findById(id).isPresent()) {
                if (!Sim.stopSimulation(true))
                    throw new IOException("Simulation is not running.");
            } else {
                throw new IOException("Event not found with id " + id);
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Save logs for an event.
     *
     * @param id the event ID
     */
    public void saveLogs(String id) {
        lock.lock();
        try {
            eventRepo.findById(id).ifPresent(event -> {
                event.getLogs().add(SimLog.log);
                event.getIntLogs().add(SimLog.logInt);
                eventRepo.save(event);
            });
        } finally {
            lock.unlock();
        }
    }

    /**
     * Get the configuration for an event.
     *
     * @param id the event ID
     * @return the event configuration
     * @throws IOException if the event is not found
     */
    public List<Integer> getConfig(String id) throws IOException {
        return eventRepo.findById(id).orElseThrow(() -> new IOException("Event not found with id " + id)).getConfig();
    }
}