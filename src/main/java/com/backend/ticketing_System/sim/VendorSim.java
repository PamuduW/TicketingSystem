package com.backend.ticketing_System.sim;

import lombok.Getter;
import lombok.Setter;

import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

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

    public VendorSim(int vendorReleaseRate, EventSim eventSim) {
        this.vendorName = "V" + vendorCount++;
        this.vendorReleaseRate = vendorReleaseRate;
        this.eventSim = eventSim;
    }

    @Override
    public void run() {
        Random random = new Random();
        while (totalTicketsAdded < totalTicketLimit) {
            try {
                Thread.sleep(simulationSpeed);
                int ticketsToAdd = random.nextInt(vendorReleaseRate) + 1;
                lock.lock();
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
                    if (totalTicketsAdded + ticketsToAdd > totalTicketLimit) {
                        ticketsToAdd = totalTicketLimit - totalTicketsAdded;
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
