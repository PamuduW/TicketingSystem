package com.backend.ticketing_System.sim;

import com.backend.ticketing_System.handler.IntegerWebSocketHandler;
import com.backend.ticketing_System.handler.TextWebSocketHandler;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class EventSim {
    private int currentTickets = 0;
    private int allSoldTickets = 0;
    private final int maxCapacity;
    private final ReentrantLock lock = new ReentrantLock(true);
    private final Condition poolFull = lock.newCondition();
    private final Condition poolEmpty = lock.newCondition();


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
            TextWebSocketHandler.broadcast(String.format("--- Vendor %s --- trying to add %d tickets", vendorName, ticketsToAdd));
            while (currentTickets + ticketsToAdd > maxCapacity) {
                try {
                    TextWebSocketHandler.broadcast("--- Vendor " + vendorName + " ---  Waiting, ticket pool at max capacity.");
                    poolFull.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            currentTickets += ticketsToAdd;
            TextWebSocketHandler.broadcast(String.format("--- Vendor %s --- Added %d tickets. Current tickets : %d. All sold Tickets : %d", vendorName, ticketsToAdd, currentTickets, allSoldTickets));
            IntegerWebSocketHandler.broadcast(currentTickets, allSoldTickets);
            poolEmpty.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void retrieveTickets(String customerName, int ticketsToRetrieve, boolean finalTransaction) {
        lock.lock();
        try {
            if (allSoldTickets == VendorSim.getTotalTicketLimit()) return;
            TextWebSocketHandler.broadcast(String.format("--- Customer %s --- trying to retrieve %d tickets", customerName, ticketsToRetrieve));
            if (currentTickets < ticketsToRetrieve) {
                if (finalTransaction) {
                    allSoldTickets += currentTickets;
                    TextWebSocketHandler.broadcast(String.format("--- Customer %s --- Retrieved remaining %d tickets. Current tickets : 0. All sold Tickets : %d. (final transaction)", customerName, currentTickets, allSoldTickets));
                    currentTickets = 0;
                    IntegerWebSocketHandler.broadcast(currentTickets, allSoldTickets);
                    return;
                } else {
                    while (currentTickets < ticketsToRetrieve) {
                        try {
                            TextWebSocketHandler.broadcast("--- Customer " + customerName + " --- Waiting, Not enough tickets.");
                            poolEmpty.await();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }
            currentTickets -= ticketsToRetrieve;
            allSoldTickets += ticketsToRetrieve;
            TextWebSocketHandler.broadcast(String.format("--- Customer %s --- Retrieved %d tickets. Current tickets : %d. All sold Tickets : %d", customerName, ticketsToRetrieve, currentTickets, allSoldTickets));
            IntegerWebSocketHandler.broadcast(currentTickets, allSoldTickets);
            poolFull.signalAll();
        } finally {
            lock.unlock();
        }
    }
}
