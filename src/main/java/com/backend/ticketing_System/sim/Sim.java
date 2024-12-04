package com.backend.ticketing_System.sim;

import com.backend.ticketing_System.handler.TextWebSocket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class Sim {
    private static final List<Thread> vendorThreads = Collections.synchronizedList(new ArrayList<>());
    private static final List<Thread> customerThreads = Collections.synchronizedList(new ArrayList<>());
    private static final ReentrantLock lock = new ReentrantLock(true);
    public static boolean activeVendors;
    private static boolean isRunning = false;


    public static void startSimulation(int totalTickets, int vendorReleaseRate, int customerRetrievalRate, int maxTicketCapacity, int noOfVendors, int noOfCustomers, int simSpeed) {
        if (isRunning) {
            System.out.println("Simulation is already running.");
            return;
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

        for (int i = 0; i < noOfVendors; i++) {
            Thread vendorThread = new Thread(new VendorSim(vendorReleaseRate, eventSim));
            vendorThreads.add(vendorThread);
            vendorThread.start();
        }

        for (int i = 0; i < noOfCustomers; i++) {
            Thread customerThread = new Thread(new CustomerSim(customerRetrievalRate, eventSim));
            customerThreads.add(customerThread);
            customerThread.start();
        }
        System.out.println("Simulation started.");
    }

    public static void stopSimulation(boolean message) {
        lock.lock();
        try {
            if (!isRunning) {
                System.out.println("Simulation is not running.");
                return;
            }

            vendorThreads.forEach(Thread::interrupt);
            customerThreads.forEach(Thread::interrupt);
            vendorThreads.clear();
            customerThreads.clear();

            isRunning = false;
            if (message) {
                System.out.println("Simulation stopped by the user.");
                TextWebSocket.broadcast("Simulation stopped by the user.");
            }
        } finally {
            lock.unlock();
        }
    }
}
