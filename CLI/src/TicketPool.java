import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The TicketPool class manages a pool of tickets with a maximum capacity.
 * It allows vendors to add tickets and customers to retrieve tickets.
 * The class uses a ReentrantLock to ensure thread safety.
 */
public class TicketPool {
    private int currentTickets = 0;
    private int allSoldTickets = 0;
    private final int maxCapacity;
    private final Console console;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition poolFull = lock.newCondition();
    private final Condition poolEmpty = lock.newCondition();

    /**
     * Constructs a TicketPool with the specified maximum capacity and console for logging.
     *
     * @param maxCapacity the maximum number of tickets the pool can hold
     * @param console the console for logging output
     */
    public TicketPool(int maxCapacity, Console console) {
        this.maxCapacity = maxCapacity;
        this.console = console;
    }

    /**
     * Gets the current number of tickets in the pool.
     *
     * @return the current number of tickets
     */
    public int getTicketCount() {
        lock.lock();
        try {
            return currentTickets;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Adds tickets to the pool. If the pool is full, the vendor waits until there is space.
     *
     * @param vendorName the name of the vendor adding tickets
     * @param ticketsToAdd the number of tickets to add
     */
    public void addTickets(String vendorName, int ticketsToAdd) {
        lock.lock();
        try {
            console.appendOutput(String.format("--- Vendor %s --- trying to add %d tickets", vendorName, ticketsToAdd));
            while (currentTickets + ticketsToAdd > maxCapacity) {
                try {
                    console.appendOutput("--- Vendor " + vendorName + " ---  Waiting, ticket pool at max capacity.");
                    poolFull.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            currentTickets += ticketsToAdd;
            console.appendOutput(String.format("--- Vendor %s --- Added %d tickets. Current tickets: %d. All sold Tickets: %d", vendorName, ticketsToAdd, currentTickets, allSoldTickets));
            poolEmpty.signalAll();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Retrieves tickets from the pool. If there are not enough tickets, the customer waits until there are enough.
     * If it is the final transaction, retrieves all remaining tickets.
     *
     * @param customerName the name of the customer retrieving tickets
     * @param ticketsToRetrieve the number of tickets to retrieve
     * @param finalTransaction true if this is the final transaction, false otherwise
     */
    public void retrieveTickets(String customerName, int ticketsToRetrieve, boolean finalTransaction) {
        lock.lock();
        try {
            if (allSoldTickets == Vendor.getTotalTicketLimit()) return;
            console.appendOutput(String.format("--- Customer %s --- trying to retrieve %d tickets", customerName, ticketsToRetrieve));
            if (currentTickets < ticketsToRetrieve) {
                if (finalTransaction) {
                    allSoldTickets += currentTickets;
                    console.appendOutput(String.format("--- Customer %s --- Retrieved remaining %d tickets. Current tickets: 0. All sold Tickets: %d. (final transaction)", customerName, currentTickets, allSoldTickets));
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
            currentTickets -= ticketsToRetrieve;
            allSoldTickets += ticketsToRetrieve;
            console.appendOutput(String.format("--- Customer %s --- Retrieved %d tickets. Current tickets: %d. All sold Tickets: %d", customerName, ticketsToRetrieve, currentTickets, allSoldTickets));
            poolFull.signalAll();
        } finally {
            lock.unlock();
        }
    }
}