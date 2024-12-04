import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class Vendor implements Runnable {
    private final String vendorName; // Name of the vendor
    private final int vendorReleaseRate; // Maximum number of tickets the vendor can release
    private final int simulationSpeed; // Speed of the simulation for the vendor
    private final TicketPool ticketPool; // Ticket pool to which the vendor adds tickets
    private final Console console; // Console for logging output
    private static int totalTicketLimit; // Total limit of tickets that can be added by all vendors
    private static int totalTicketsAdded = 0; // Total number of tickets added by all vendors
    private static int vendorCount = 0; // Counter to assign unique names to vendors
    private static boolean messagePrinted = false; // Flag to indicate if the final message has been printed
    private static final ReentrantLock lock = new ReentrantLock(); // Lock for synchronizing access to shared resources

    // Constructor to initialize the vendor with the maximum tickets to release, ticket pool, console, and simulation speed
    public Vendor(int vendorReleaseRate, TicketPool ticketPool, Console console, int simulationSpeed) {
        this.vendorName = "V" + vendorCount++;
        this.vendorReleaseRate = vendorReleaseRate;
        this.ticketPool = ticketPool;
        this.console = console;
        this.simulationSpeed = simulationSpeed;
    }

    // Method to set the total ticket limit
    public static void setTotalTicketLimit(int totalTicketLimit) {
        Vendor.totalTicketLimit = totalTicketLimit;
    }

    // Method to set the message printed flag
    public static void setMessagePrinted(boolean messagePrinted) {
        Vendor.messagePrinted = messagePrinted;
    }

    // Method to set the total tickets added
    public static void setTotalTicketsAdded(int totalTicketsAdded) {
        Vendor.totalTicketsAdded = totalTicketsAdded;
    }

    // Method to get the total ticket limit
    public static int getTotalTicketLimit() {
        return totalTicketLimit;
    }

    // Method to get the total tickets added
    public static int getTotalTicketsAdded() {
        return totalTicketsAdded;
    }

    // Method to set the vendor count
    public static void setVendorCount(int vendorCount) {
        Vendor.vendorCount = vendorCount;
    }

    @Override
    public void run() {
        Random random = new Random();
        while (totalTicketsAdded < totalTicketLimit) {
            try {
                Thread.sleep(simulationSpeed); // Simulate vendor interaction speed
                int ticketsToAdd = random.nextInt(vendorReleaseRate) + 1; // Random number of tickets to add

                lock.lock();
                try {
                    Thread.sleep(simulationSpeed); // Simulate vendor interaction speed
                    // Check if the total tickets added has reached the limit
                    if (totalTicketsAdded == totalTicketLimit) {
                        if (!messagePrinted) {
                            console.appendOutput("All vendors have reached the ticket limit and stopped interacting.");
                            Main.activeVendors = false;
                            messagePrinted = true;
                        }
                        break;
                    }
                    // Adjust the number of tickets to add if it exceeds the total ticket limit
                    if (totalTicketsAdded + ticketsToAdd > totalTicketLimit) {
                        ticketsToAdd = totalTicketLimit - totalTicketsAdded;
                    }
                    ticketPool.addTickets(vendorName, ticketsToAdd); // Add tickets to the pool
                    totalTicketsAdded += ticketsToAdd; // Update the total tickets added
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Handle thread interruption
            }
        }
    }
}