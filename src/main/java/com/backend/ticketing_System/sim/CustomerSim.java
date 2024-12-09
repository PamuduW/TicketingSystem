package com.backend.ticketing_System.sim;

import lombok.Setter;

import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

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
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public CustomerSim(int maxTicketsToRetrieve, EventSim eventSim) {
        this.customerName = "C" + customerCount++;
        this.customerRetrievalRate = maxTicketsToRetrieve;
        this.eventSim = eventSim;
    }

    @Override
    public void run() {
        Random random = new Random();
        while (!finalTransaction) {
            try {
                Thread.sleep(simulationSpeed);
                int ticketsToRetrieve = random.nextInt(customerRetrievalRate) + 1;

                lock.lock();
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
                        Sim simInstance = new Sim();
                        simInstance.stopSimulation(false, Sim.eventId); // Replace "eventId" with the actual event ID
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