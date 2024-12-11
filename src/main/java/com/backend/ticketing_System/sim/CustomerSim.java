package com.backend.ticketing_System.sim;

import lombok.Setter;

import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Simulates customer behavior in the ticketing system.
 */
public class CustomerSim implements Runnable {
    @Setter
    private String customerName;
    private final int customerRetrievalRate;
    @Setter
    private static int simulationSpeed;
    private final EventSim eventSim;
    @Setter
    private static int customerCount = 0;
    @Setter
    private static boolean messagePrinted = false;
    @Setter
    private static boolean finalTransaction = false;
    private static final ReentrantLock lock = new ReentrantLock(true);

    /**
     * Constructs a new CustomerSim with the specified maximum tickets to retrieve and event simulation.
     *
     * @param maxTicketsToRetrieve the maximum number of tickets a customer can retrieve.
     * @param eventSim the event simulation instance.
     */
    public CustomerSim(int maxTicketsToRetrieve, EventSim eventSim) {
        this.customerName = "C" + customerCount++;
        this.customerRetrievalRate = maxTicketsToRetrieve;
        this.eventSim = eventSim;
    }

    /**
     * Runs the customer simulation.
     * Continuously attempts to retrieve tickets until the final transaction is completed.
     */
    @Override
    public void run() {
        Random random = new Random();
        while (!finalTransaction) {
            try {
                Thread.sleep(simulationSpeed);
                lock.lock();
                int ticketsToRetrieve = random.nextInt(customerRetrievalRate) + 1;
                try {
                    Thread.sleep(simulationSpeed);
                    if (Sim.activeVendors && ticketsToRetrieve > VendorSim.getTotalTicketLimit() - VendorSim.getTotalTicketsAdded()) {
                        continue;
                    } else if (!Sim.activeVendors && ticketsToRetrieve >= eventSim.getTicketCount()) {
                        finalTransaction = true;
                    }
                    eventSim.retrieveTickets(customerName, ticketsToRetrieve, finalTransaction);

                    if (finalTransaction && !messagePrinted) {
                        SimLog.logging("--- Customer " + customerName + " >>> Retrieved the last available tickets. \n\s\s\sEnding simulation.");
                        Sim.stopSimulation(false);
                        messagePrinted = true;
                        break;
                    }
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}