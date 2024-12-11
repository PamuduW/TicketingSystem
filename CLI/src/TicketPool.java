import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class TicketPool {
    private int currentTickets = 0;
    private int allSoldTickets = 0;
    private final int maxCapacity;
    private final Console console;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition poolFull = lock.newCondition();
    private final Condition poolEmpty = lock.newCondition();

    public TicketPool(int maxCapacity, Console console) {
        this.maxCapacity = maxCapacity;
        this.console = console;
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
            console.appendOutput(String.format("--- Vendor %s --- Added %d tickets. Current tickets : %d. All sold Tickets : %d", vendorName, ticketsToAdd, currentTickets, allSoldTickets));
            poolEmpty.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void retrieveTickets(String customerName, int ticketsToRetrieve, boolean finalTransaction) {
        lock.lock();
        try {
            if (allSoldTickets == Vendor.getTotalTicketLimit()) return;
            console.appendOutput(String.format("--- Customer %s --- trying to retrieve %d tickets", customerName, ticketsToRetrieve));
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
            currentTickets -= ticketsToRetrieve;
            allSoldTickets += ticketsToRetrieve;
            console.appendOutput(String.format("--- Customer %s --- Retrieved %d tickets. Current tickets : %d. All sold Tickets : %d", customerName, ticketsToRetrieve, currentTickets, allSoldTickets));
            poolFull.signalAll();
        } finally {
            lock.unlock();
        }
    }
}