package com.backend.ticketing_System.sim;

import java.util.ArrayList;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Simulates the ticketing system.
 */
public class Sim {
    private static ThreadPoolExecutor threadPoolExecutor;
    private static final ReentrantLock lock = new ReentrantLock(true);
    public static boolean activeVendors;
    private static boolean isRunning = false;
    public static String eventId;

    /**
     * Starts the simulation with the given parameters.
     *
     * @param totalTickets the total number of tickets.
     * @param vendorReleaseRate the rate at which vendors release tickets.
     * @param customerRetrievalRate the rate at which customers retrieve tickets.
     * @param maxTicketCapacity the maximum capacity of tickets.
     * @param noOfVendors the number of vendors.
     * @param noOfCustomers the number of customers.
     * @param noOfVIPCustomers the number of VIP customers.
     * @param simSpeed the speed of the simulation.
     * @param eventId the ID of the event.
     * @return true if the simulation started successfully, false otherwise.
     */
    public static boolean startSimulation(int totalTickets, int vendorReleaseRate, int customerRetrievalRate, int maxTicketCapacity, int noOfVendors, int noOfCustomers, int noOfVIPCustomers, int simSpeed, String eventId) {
        lock.lock();
        try {
            if (isRunning) return false;

            activeVendors = true;
            isRunning = true;
            Sim.eventId = eventId;

            VendorSim.setSimulationSpeed(simSpeed);
            VendorSim.setTotalTicketLimit(totalTickets);
            EventSim eventSim = new EventSim(maxTicketCapacity);
            resetVendorSim();
            resetCustomerSim(simSpeed);

            SimLog.log = "";
            SimLog.logInt = new ArrayList<>();

            int totalThreads = noOfVendors + noOfVIPCustomers + noOfCustomers;
            threadPoolExecutor = new ThreadPoolExecutor(totalThreads, totalThreads, 0L, TimeUnit.MILLISECONDS, new PriorityBlockingQueue<>());

            submitTasks(noOfVendors, vendorReleaseRate, eventSim, noOfCustomers, customerRetrievalRate, noOfVIPCustomers);

            SimLog.logging("--- Simulation started.");
        } finally {
            lock.unlock();
        }
        return true;
    }

    /**
     * Stops the simulation.
     *
     * @param message whether to log a stop message.
     * @return true if the simulation stopped successfully, false otherwise.
     */
    public static boolean stopSimulation(boolean message) {
        lock.lock();
        try {
            if (!isRunning) return false;

            threadPoolExecutor.shutdownNow();
            isRunning = false;
            if (message) SimLog.logging("--- Simulation stopped by the user.");
        } finally {
            lock.unlock();
        }
        return true;
    }

    /**
     * Resets the vendor simulation.
     */
    private static void resetVendorSim() {
        VendorSim.setMessagePrinted(false);
        VendorSim.setTotalTicketsAdded(0);
        VendorSim.setVendorCount(0);
    }

    /**
     * Resets the customer simulation.
     *
     * @param simSpeed the speed of the simulation.
     */
    private static void resetCustomerSim(int simSpeed) {
        CustomerSim.setSimulationSpeed(simSpeed);
        CustomerSim.setCustomerCount(0);
        CustomerSim.setFinalTransaction(false);
        CustomerSim.setMessagePrinted(false);
    }

    /**
     * Submits tasks to the thread pool executor.
     *
     * @param noOfVendors the number of vendors.
     * @param vendorReleaseRate the rate at which vendors release tickets.
     * @param eventSim the event simulation instance.
     * @param noOfCustomers the number of customers.
     * @param customerRetrievalRate the rate at which customers retrieve tickets.
     * @param noOfVIPCustomers the number of VIP customers.
     */
    private static void submitTasks(int noOfVendors, int vendorReleaseRate, EventSim eventSim, int noOfCustomers, int customerRetrievalRate, int noOfVIPCustomers) {
        for (int i = 0; i < noOfVendors; i++) {
            threadPoolExecutor.submit(new VendorSim(vendorReleaseRate, eventSim));
        }
        for (int i = 0; i < noOfCustomers; i++) {
            threadPoolExecutor.submit(new CustomerTaskSim(new CustomerSim(customerRetrievalRate, eventSim), false));
        }
        for (int i = 0; i < noOfVIPCustomers; i++) {
            threadPoolExecutor.submit(new CustomerTaskSim(new VIPCustomerSim(customerRetrievalRate, eventSim), true));
        }
    }
}