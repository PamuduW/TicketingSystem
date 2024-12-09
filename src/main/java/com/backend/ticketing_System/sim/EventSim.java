package com.backend.ticketing_System.sim;

import java.time.format.DateTimeFormatter;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class EventSim {
    private int currentTickets = 0;
    private int allSoldTickets = 0;
    private final int maxCapacity;
    private final ReentrantLock lock = new ReentrantLock(true);
    private final Condition poolFull = lock.newCondition();
    private final Condition poolEmpty = lock.newCondition();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    public EventSim(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public int getTicketCount() {
        lock.lock();
        try {
            return currentTickets;
        } finally {
            lock.unlock();
        }
    }

    public void addTickets(String vendorName, int ticketsToAdd) {
        lock.lock();
        try {
            SimLog.logging(String.format("--- Vendor %s >>> trying to add %d tickets", vendorName, ticketsToAdd));
            while (currentTickets + ticketsToAdd > maxCapacity) {
                try {
                    SimLog.logging("--- Vendor " + vendorName + " >>>  Waiting, ticket pool at max capacity.");
                    poolFull.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            currentTickets += ticketsToAdd;
            SimLog.loggingWithNums(String.format("--- Vendor %s >>> Added %d tickets. Current tickets : %d. All sold Tickets : %d", vendorName, ticketsToAdd, currentTickets, allSoldTickets), currentTickets, allSoldTickets, false);
            poolEmpty.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void retrieveTickets(String customerName, int ticketsToRetrieve, boolean finalTransaction) {
        lock.lock();
        try {
            if (allSoldTickets == VendorSim.getTotalTicketLimit()) return;
            SimLog.logging(String.format("--- Customer %s >>> trying to retrieve %d tickets", customerName, ticketsToRetrieve));
            if (currentTickets < ticketsToRetrieve) {
                if (finalTransaction) {
                    allSoldTickets += currentTickets;
                    SimLog.loggingWithNums(String.format("--- Customer %s >>> Retrieved remaining %d tickets. Current tickets : 0. All sold Tickets : %d. (final transaction)", customerName, currentTickets, allSoldTickets), currentTickets, allSoldTickets, true);
                    return;
                } else {
                    while (currentTickets < ticketsToRetrieve) {
                        try {
                            SimLog.logging("--- Customer " + customerName + " >>> Waiting, Not enough tickets.");
                            poolEmpty.await();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }
            currentTickets -= ticketsToRetrieve;
            allSoldTickets += ticketsToRetrieve;
            SimLog.loggingWithNums(String.format("--- Customer %s >>> Retrieved %d tickets. Current tickets : %d. All sold Tickets : %d", customerName, ticketsToRetrieve, currentTickets, allSoldTickets), currentTickets, allSoldTickets, false);
            poolFull.signalAll();
        } finally {
            lock.unlock();
        }
    }
}
