package com.backend.ticketing_System.sim;

import com.backend.ticketing_System.model.Event;
import com.backend.ticketing_System.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class Sim {
    private static ThreadPoolExecutor threadPoolExecutor;
    private static final ReentrantLock lock = new ReentrantLock(true);
    public static boolean activeVendors;
    private static boolean isRunning = false;
    public static String eventId;

    @Autowired
    private EventRepository eventRepository;

    public static boolean startSimulation(int totalTickets, int vendorReleaseRate, int customerRetrievalRate, int maxTicketCapacity, int noOfVendors, int noOfCustomers, int noOfVIPCustomers, int simSpeed, String eventId) {
        lock.lock();
        Sim.eventId = eventId;
        try {
            if (isRunning) {
                return false;
            }

            activeVendors = true;
            isRunning = true;

            VendorSim.setSimulationSpeed(simSpeed);
            VendorSim.setTotalTicketLimit(totalTickets);
            EventSim eventSim = new EventSim(maxTicketCapacity);
            VendorSim.setMessagePrinted(false);
            VendorSim.setTotalTicketsAdded(0);
            VendorSim.setVendorCount(0);
            CustomerSim.setSimulationSpeed(simSpeed);
            CustomerSim.setCustomerCount(0);
            CustomerSim.setFinalTransaction(false);
            CustomerSim.setMessagePrinted(false);

            threadPoolExecutor = new ThreadPoolExecutor(
                    noOfVendors + noOfVIPCustomers + noOfCustomers,
                    noOfVendors + noOfVIPCustomers + noOfCustomers,
                    0L, TimeUnit.MILLISECONDS,
                    new PriorityBlockingQueue<>()
            );

            for (int i = 0; i < noOfVendors; i++) {
                threadPoolExecutor.submit(new VendorSim(vendorReleaseRate, eventSim));
            }
            for (int i = 0; i < noOfCustomers; i++) {
                threadPoolExecutor.submit(new CustomerTaskSim(new CustomerSim(customerRetrievalRate, eventSim), false));
            }
            for (int i = 0; i < noOfVIPCustomers; i++) {
                threadPoolExecutor.submit(new CustomerTaskSim(new VIPCustomerSim(customerRetrievalRate, eventSim), true));
            }
            SimLog.logging("--- Simulation started.");
        } finally {
            lock.unlock();
        }
        return true;
    }

    public boolean stopSimulation(boolean message, String eventId) {
        lock.lock();
        try {
            if (!isRunning) {
                return false;
            }

            threadPoolExecutor.shutdownNow();

            isRunning = false;
            if (message) {
                SimLog.logging("--- Simulation stopped by the user.");
            }

            // Retrieve the event by ID
            if (eventRepository.findById(eventId).isPresent()){
                System.out.println("//////////////////////////////////////////////////////////");

                Event event = eventRepository.findById(eventId).get();

                event.getLogs().add(SimLog.log);
                event.getIntLogs().add(SimLog.logInt);

                eventRepository.save(event);
            } else
                System.out.println("------------------------------------------------------");
        } finally {
            lock.unlock();
        }
        return true;
    }
}