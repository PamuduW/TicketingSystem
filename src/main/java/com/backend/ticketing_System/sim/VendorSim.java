package com.backend.ticketing_System.sim;

import lombok.Getter;
import lombok.Setter;

import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Simulates vendor behavior in the ticketing system.
 */
public class VendorSim implements Runnable {
    private final String vendorName;
    private final int vendorReleaseRate;
    @Setter
    private static int simulationSpeed;
    private final EventSim eventSim;
    @Getter
    @Setter
    private static int totalTicketLimit;
    @Getter
    @Setter
    private static int totalTicketsAdded = 0;
    @Setter
    private static int vendorCount = 0;
    @Setter
    private static boolean messagePrinted = false;
    private static final ReentrantLock lock = new ReentrantLock(true);

    /**
     * Constructs a new VendorSim with the specified release rate and event simulation.
     *
     * @param vendorReleaseRate the rate at which the vendor releases tickets.
     * @param eventSim the event simulation instance.
     */
    public VendorSim(int vendorReleaseRate, EventSim eventSim) {
        this.vendorName = "V" + vendorCount++;
        this.vendorReleaseRate = vendorReleaseRate;
        this.eventSim = eventSim;
    }

    /**
     * Runs the vendor simulation.
     * Continuously attempts to add tickets until the total ticket limit is reached.
     */
    @Override
    public void run() {
        Random random = new Random();
        while (totalTicketsAdded < totalTicketLimit) {
            try {
                Thread.sleep(simulationSpeed);
                lock.lock();
                int ticketsToAdd = Math.min(random.nextInt(vendorReleaseRate) + 1, totalTicketLimit - totalTicketsAdded);
                try {
                    Thread.sleep(simulationSpeed);
                    if (totalTicketsAdded == totalTicketLimit) {
                        if (!messagePrinted) {
                            SimLog.logging("--- All vendors have reached the ticket limit and stopped interacting.");
                            Sim.activeVendors = false;
                            messagePrinted = true;
                        }
                        break;
                    }
                    eventSim.addTickets(vendorName, ticketsToAdd);
                    totalTicketsAdded += ticketsToAdd;
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}