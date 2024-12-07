package com.backend.ticketing_System.service;

import com.backend.ticketing_System.model.Event;
import com.backend.ticketing_System.model.Ticket;
import com.backend.ticketing_System.model.Vendor;
import com.backend.ticketing_System.repository.EventRepository;
import com.backend.ticketing_System.repository.VendorRepository;
import com.backend.ticketing_System.sim.Sim;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class EventService {
    private final EventRepository eventRepo;
    private final VendorRepository vendorRepo;
    private final ReentrantLock lock = new ReentrantLock(true);

    @Autowired
    public EventService(EventRepository eventRepo, VendorRepository vendorRepo) {
        this.eventRepo = eventRepo;
        this.vendorRepo = vendorRepo;
    }

    public List<Event> getAllEvents() {
        return eventRepo.findAll();
    }

    public Optional<Event> getEventById(String eventId) {
        return eventRepo.findById(eventId);
    }

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

    public List<Event> getAllByOwnerId(String ownerId) {
        return eventRepo.findAllByOwnerId(ownerId);
    }

    public List<Event> getAllByVendorId(String vendorId) {
        List<Event> list = new ArrayList<>();
        for (Event event : eventRepo.findAll()) {
            for (String optionalVendorId : event.getVendors()) {
                if (optionalVendorId.equals(vendorId)) {
                    list.add(event);
                    break;
                }
            }
        }
        return list;
    }

    public Event updateEvent(String eventId, Event eventDetails) throws IOException {
        lock.lock();
        try {
            Optional<Event> optionalEvent = eventRepo.findById(eventId);
            if (optionalEvent.isPresent()) {
                Event event = optionalEvent.get();
                event.setName(eventDetails.getName());
                event.setDesc(eventDetails.getDesc());
                event.setTotalTickets(eventDetails.getTotalTickets());
                event.setMaxCapacity(eventDetails.getMaxCapacity());
                return eventRepo.save(event);
            } else {
                throw new IOException("Event not found with id " + eventId);
            }
        } finally {
            lock.unlock();
        }
    }

    public Event updateEventVendors(String eventId, List<String> vendors) throws IOException {
        lock.lock();
        try {
            Optional<Event> optionalEvent = eventRepo.findById(eventId);
            if (optionalEvent.isPresent()) {
                Event event = optionalEvent.get();
                Set<String> set = new HashSet<>();
                for (String vendorId : vendors) {
                    Optional<Vendor> optionalVendor = vendorRepo.findById(vendorId);
                    if (optionalVendor.isPresent()) {
                        Vendor vendor = optionalVendor.get();
                        set.add(vendor.getVendorId());
                    }
                }
                event.setVendors(set);
                return eventRepo.save(event);
            } else {
                throw new IOException("Event not found with id " + eventId);
            }
        } finally {
            lock.unlock();
        }
    }

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

    public Dictionary<String, List<String>> getTicketsByCustomerId(String customerId) {
        Dictionary<String, List<String>> dic = new Hashtable<>();
        for (Event event : eventRepo.findAll()) {
            List<String> list = new ArrayList<>();
            for (Ticket ticket : event.getTickets()) {
                if (ticket.getCustomerId() == null)
                    continue;
                if (ticket.getCustomerId().equals(customerId))
                    list.add(ticket.getTicketId());
            }
            dic.put(event.getEventId(), list);
        }
        return dic;
    }

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

    public void startSimulation(String eventID, int vendorReleaseRate, int customerRetrievalRate, int noOfVendors, int noOfCustomers, int noOfVIPCustomers, int simSpeed) throws IOException {
        lock.lock();
        try {
            if (eventRepo.findById(eventID).isPresent()) {
                int totalTickets = eventRepo.findById(eventID).get().getTotalTickets();
                int maxTicketCapacity = eventRepo.findById(eventID).get().getMaxCapacity();
                if (!Sim.startSimulation(totalTickets, vendorReleaseRate, customerRetrievalRate, maxTicketCapacity, noOfVendors, noOfCustomers, noOfVIPCustomers, simSpeed))
                    throw new IOException("Simulation is already running.");
            } else throw new IOException("Event not found with id " + eventID);
        } finally {
            lock.unlock();
        }
    }

    public void stopSimulation(String id) throws IOException {
        lock.lock();
        try {
            if (!Sim.stopSimulation(true))
                throw new IOException("Simulation is not running.");
        } finally {
            lock.unlock();
        }
    }

}


/// //////////////////////////////////////////// make only one instance of vendor acc log in can happen at any time ///////////////////////////////////////////////////////
/// //////////////////////////////////////////// make stop sim advanced check if the event exists///////////////////////////////////////////////////////////////////////
/// ///////////////////////////////////////// try to extend customers into VIPs and give them early access ( like till certain num of tickets are sold or something like that. after that all are equal. try to implement this for both sim and real world )   /////////////////////////////
/// /////////////////////////////////////////try to add image functions  ////////////////////////////////////////////////////////////////////////////
/// ////////////////////////////////////////// try to make sims web socket independent to the session ///////////////////////////////////////////////
/// //////////////////////////////////////// save a logs in the backend ////////////////////////////////////////

/// /////////////////////////////////////////// give real time variables to the front end to display //////////////////////////////////
/// //////////////////////////////////////////////////////////// add editing event details make owner id remains unchanged //////////////////////////////////////////////////////////////
/// //////
/// ////////////////////////////////// add stop sim button //////////////////////////////////////////////////
/// /////////////////////////////////////////// add time to that update stings in console//////////////////////////////////////
