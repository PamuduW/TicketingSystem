import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The Vendor class represents a vendor that adds tickets to a ticket pool at a specified rate.
 * Implements the Runnable interface to allow execution in a separate thread.
 */
public class Vendor implements Runnable {
    private final String vendorName;
    private final int vendorReleaseRate;
    private final int simulationSpeed;
    private final TicketPool ticketPool;
    private final Console console;
    private static int totalTicketLimit;
    private static int totalTicketsAdded = 0;
    private static int vendorCount = 0;
    private static boolean messagePrinted = false;
    private static final ReentrantLock lock = new ReentrantLock();

    /**
     * Constructs a Vendor with the specified release rate, ticket pool, console, and simulation speed.
     *
     * @param vendorReleaseRate the maximum number of tickets the vendor can release at a time
     * @param ticketPool the ticket pool to which the vendor adds tickets
     * @param console the console for logging output
     * @param simulationSpeed the speed of the simulation in milliseconds
     */
    public Vendor(int vendorReleaseRate, TicketPool ticketPool, Console console, int simulationSpeed) {
        this.vendorName = "V" + vendorCount++;
        this.vendorReleaseRate = vendorReleaseRate;
        this.ticketPool = ticketPool;
        this.console = console;
        this.simulationSpeed = simulationSpeed;
    }

    /**
     * Sets the total ticket limit for all vendors.
     *
     * @param totalTicketLimit the total number of tickets that can be added by all vendors
     */
    public static void setTotalTicketLimit(int totalTicketLimit) {
        Vendor.totalTicketLimit = totalTicketLimit;
    }

    /**
     * Sets the message printed flag.
     *
     * @param messagePrinted true if the message has been printed, false otherwise
     */
    public static void setMessagePrinted(boolean messagePrinted) {
        Vendor.messagePrinted = messagePrinted;
    }

    /**
     * Sets the total number of tickets added by all vendors.
     *
     * @param totalTicketsAdded the total number of tickets added
     */
    public static void setTotalTicketsAdded(int totalTicketsAdded) {
        Vendor.totalTicketsAdded = totalTicketsAdded;
    }

    /**
     * Gets the total ticket limit for all vendors.
     *
     * @return the total ticket limit
     */
    public static int getTotalTicketLimit() {
        return totalTicketLimit;
    }

    /**
     * Gets the total number of tickets added by all vendors.
     *
     * @return the total number of tickets added
     */
    public static int getTotalTicketsAdded() {
        return totalTicketsAdded;
    }

    /**
     * Sets the vendor count.
     *
     * @param vendorCount the number of vendors
     */
    public static void setVendorCount(int vendorCount) {
        Vendor.vendorCount = vendorCount;
    }

    /**
     * The run method for the Vendor thread. Adds tickets to the ticket pool at random intervals
     * until the total ticket limit is reached.
     */
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
                            console.appendOutput("All vendors have reached the ticket limit and stopped interacting.");
                            Main.activeVendors = false;
                            messagePrinted = true;
                        }
                        break;
                    }
                    if (totalTicketsAdded + ticketsToAdd > totalTicketLimit) {
                        ticketsToAdd = totalTicketLimit - totalTicketsAdded;
                    }
                    ticketPool.addTickets(vendorName, ticketsToAdd);
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