import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class TicketPool {
    private int currentTickets = 0; // Current number of tickets in the pool
    private int allSoldTickets = 0; // Total number of tickets sold
    private final int maxCapacity; // Maximum capacity of the ticket pool
    private final Console console; // Console for logging output
    private final ReentrantLock lock = new ReentrantLock(); // Lock for synchronizing access to the ticket pool
    private final Condition poolFull = lock.newCondition(); // Condition to wait when the pool is full
    private final Condition poolEmpty = lock.newCondition(); // Condition to wait when the pool is empty

    // Constructor to initialize the ticket pool with a maximum capacity and console for logging
    public TicketPool(int maxCapacity, Console console) {
        this.maxCapacity = maxCapacity;
        this.console = console;
    }

    // Method to get the current number of tickets in the pool
    public int getTicketCount() {
        lock.lock();
        try {
            return currentTickets;
        } finally {
            lock.unlock();
        }
    }

    // Method to add tickets to the pool
    public void addTickets(String vendorName, int ticketsToAdd) {
        lock.lock();
        try {
            console.appendOutput(String.format("--- Vendor %s --- trying to add %d tickets", vendorName, ticketsToAdd));
            // Wait if adding tickets exceeds the pool's maximum capacity
            while (currentTickets + ticketsToAdd > maxCapacity) {
                try {
                    console.appendOutput("--- Vendor " + vendorName + " ---  Waiting, ticket pool at max capacity.");
                    poolFull.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            // Add tickets to the pool
            currentTickets += ticketsToAdd;
            console.appendOutput(String.format("--- Vendor %s --- Added %d tickets. Current tickets : %d. All sold Tickets : %d", vendorName, ticketsToAdd, currentTickets, allSoldTickets));
            poolEmpty.signalAll(); // Signal that tickets have been added
        } finally {
            lock.unlock();
        }
    }

    // Method to retrieve tickets from the pool
    public void retrieveTickets(String customerName, int ticketsToRetrieve, boolean finalTransaction) {
        lock.lock();
        try {
            // Return if all tickets have been sold
            if (allSoldTickets == Vendor.getTotalTicketLimit()) return;
            console.appendOutput(String.format("--- Customer %s --- trying to retrieve %d tickets", customerName, ticketsToRetrieve));
            // Wait if there are not enough tickets for the final transaction
            if (currentTickets < ticketsToRetrieve) {
                if (finalTransaction) {
                    allSoldTickets += currentTickets;
                    console.appendOutput(String.format("--- Customer %s --- Retrieved remaining %d tickets. Current tickets : 0. All sold Tickets : %d. (final transaction)", customerName, currentTickets, allSoldTickets));
                    currentTickets = 0;
                    return;
                } else {
                    while (currentTickets < ticketsToRetrieve) {
                        try {
                            console.appendOutput("--- Customer " + customerName + " --- Waiting, Not enough tickets.");
                            poolEmpty.await();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }
            // Retrieve tickets from the pool
            currentTickets -= ticketsToRetrieve;
            allSoldTickets += ticketsToRetrieve;
            console.appendOutput(String.format("--- Customer %s --- Retrieved %d tickets. Current tickets : %d. All sold Tickets : %d", customerName, ticketsToRetrieve, currentTickets, allSoldTickets));
            poolFull.signalAll(); // Signal that tickets have been retrieved
        } finally {
            lock.unlock();
        }
    }
}