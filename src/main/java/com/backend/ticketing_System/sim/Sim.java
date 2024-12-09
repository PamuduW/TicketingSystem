package com.backend.ticketing_System.sim;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class Sim {
    private static ThreadPoolExecutor threadPoolExecutor;
    private static final ReentrantLock lock = new ReentrantLock(true);
    public static boolean activeVendors;
    private static boolean isRunning = false;

    public static boolean startSimulation(int totalTickets, int vendorReleaseRate, int customerRetrievalRate, int maxTicketCapacity, int noOfVendors, int noOfCustomers, int noOfVIPCustomers, int simSpeed) {
        lock.lock();
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

    public static boolean stopSimulation(boolean message) {
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
        } finally {
            lock.unlock();
        }
        return true;
    }
}