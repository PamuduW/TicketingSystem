package com.backend.ticketing_System.sim;

import com.backend.ticketing_System.handler.IntegerWebSocketHandler;
import com.backend.ticketing_System.handler.TextWebSocketHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
            String str = String.format("--- Vendor %s --- trying to add %d tickets", vendorName, ticketsToAdd);
            Sim.log += str + "\n";
            TextWebSocketHandler.broadcast(str);
            while (currentTickets + ticketsToAdd > maxCapacity) {
                try {
                    String str1 = "--- Vendor " + vendorName + " ---  Waiting, ticket pool at max capacity.";
                    Sim.log += str1 + "\n";
                    TextWebSocketHandler.broadcast(str1);
                    poolFull.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            currentTickets += ticketsToAdd;
            String str2 = String.format("--- Vendor %s --- Added %d tickets. Current tickets : %d. All sold Tickets : %d", vendorName, ticketsToAdd, currentTickets, allSoldTickets);
            TextWebSocketHandler.broadcast(str2);
            Sim.log += str2 + "\n";
            Sim.logInt.add(currentTickets);
            Sim.logInt.add(allSoldTickets);
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
            String str = String.format("--- Customer %s --- trying to retrieve %d tickets", customerName, ticketsToRetrieve);
            TextWebSocketHandler.broadcast(str);
            Sim.log += str + "\n";
            if (currentTickets < ticketsToRetrieve) {
                if (finalTransaction) {
                    allSoldTickets += currentTickets;
                    String str1 = String.format("--- Customer %s --- Retrieved remaining %d tickets. Current tickets : 0. All sold Tickets : %d. (final transaction)", customerName, currentTickets, allSoldTickets);
                    TextWebSocketHandler.broadcast(str1);
                    Sim.log += str1 + "\n";
                    currentTickets = 0;
                    Sim.logInt.add(currentTickets);
                    Sim.logInt.add(allSoldTickets);
                    IntegerWebSocketHandler.broadcast(currentTickets, allSoldTickets);
                    return;
                } else {
                    while (currentTickets < ticketsToRetrieve) {
                        try {
                            String str2 = "--- Customer " + customerName + " --- Waiting, Not enough tickets.";
                            TextWebSocketHandler.broadcast(str2);
                            Sim.log += str2 + "\n";
                            poolEmpty.await();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }
            currentTickets -= ticketsToRetrieve;
            allSoldTickets += ticketsToRetrieve;
            String str3 = String.format("--- Customer %s --- Retrieved %d tickets. Current tickets : %d. All sold Tickets : %d", customerName, ticketsToRetrieve, currentTickets, allSoldTickets);
            TextWebSocketHandler.broadcast(str3);
            Sim.log += str3 + "\n";
            Sim.logInt.add(currentTickets);
            Sim.logInt.add(allSoldTickets);
            IntegerWebSocketHandler.broadcast(currentTickets, allSoldTickets);
            poolFull.signalAll();
        } finally {
            lock.unlock();
        }
    }
}
