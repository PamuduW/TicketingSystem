package com.backend.ticketing_System.sim;

import com.backend.ticketing_System.handler.TextWebSocketHandler;
import com.backend.ticketing_System.repository.EventRepository;
import com.backend.ticketing_System.repository.VendorRepository;
import com.backend.ticketing_System.service.EventService;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Sim {
    private static ThreadPoolExecutor threadPoolExecutor;
    private static final ReentrantLock lock = new ReentrantLock(true);
    public static boolean activeVendors;
    private static boolean isRunning = false;
    private static String eventId;
    private static VendorRepository vendorRepo;
    private static EventRepository eventRepo;
    public static String log;
    public static List<Integer> logInt;


    public static boolean startSimulation(int totalTickets, int vendorReleaseRate, int customerRetrievalRate, int maxTicketCapacity, int noOfVendors, int noOfCustomers, int noOfVIPCustomers, int simSpeed, String eventId, EventRepository eventRepo, VendorRepository vendorRepo) {
        lock.lock();
        try {
            Sim.eventId = eventId;
            Sim.eventRepo = eventRepo;
            Sim.vendorRepo = vendorRepo;
            if (isRunning) {
                System.out.println("Simulation is already running.");
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
            String str = "--- Simulation started.";
            TextWebSocketHandler.broadcast(str);
            log += str + "\n";
        } finally {
            lock.unlock();
        }
        return true;
    }

    public static boolean stopSimulation(boolean message) {
        lock.lock();
        try {
            if (!isRunning) {
                System.out.println("Simulation is not running.");
                return false;
            }

            threadPoolExecutor.shutdownNow();

            isRunning = false;
            if (message) {
                String str = "--- Simulation stopped by the user.";
                TextWebSocketHandler.broadcast(str);
                log += str + "\n";
                EventService eventService = new EventService(eventRepo, vendorRepo);
                eventService.saveLog(eventId, log, logInt);
            }
        } finally {
            lock.unlock();
        }
        return true;
    }
}
