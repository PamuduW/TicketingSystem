import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class Customer implements Runnable {
    private String customerName; // Name of the customer
    private final int customerRetrievalRate; // Maximum number of tickets the customer can retrieve
    private final int simulationSpeed; // Speed of the simulation for the customer
    private final TicketPool ticketPool; // Ticket pool from which the customer retrieves tickets
    private final Console console; // Console for logging output
    private static int customerCount = 0; // Counter to assign unique names to customers
    private static boolean messagePrinted = false; // Flag to indicate if the final message has been printed
    private static boolean finalTransaction = false; // Flag to indicate if the final transaction has occurred
    private static final ReentrantLock lock = new ReentrantLock(); // Lock for synchronizing access to shared resources

    // Constructor to initialize the customer with the maximum tickets to retrieve, ticket pool, console, and simulation speed
    public Customer(int maxTicketsToRetrieve, TicketPool ticketPool, Console console, int simulationSpeed) {
        this.customerName = "C" + customerCount++;
        this.customerRetrievalRate = maxTicketsToRetrieve;
        this.ticketPool = ticketPool;
        this.console = console;
        this.simulationSpeed = simulationSpeed;
    }

    protected void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    // Method to set the customer count
    public static void setCustomerCount(int customerCount) {
        Customer.customerCount = customerCount;
    }

    // Method to set the final transaction flag
    public static void setFinalTransaction(boolean finalTransaction) {
        Customer.finalTransaction = finalTransaction;
    }

    // Method to set the message printed flag
    public static void setMessagePrinted(boolean messagePrinted) {
        Customer.messagePrinted = messagePrinted;
    }

    @Override
    public void run() {
        Random random = new Random();
        while (!finalTransaction) {
            try {
                Thread.sleep(simulationSpeed); // Simulate customer interaction speed
                int ticketsToRetrieve = random.nextInt(customerRetrievalRate) + 1; // Random number of tickets to retrieve

                lock.lock();
                try {
                    Thread.sleep(simulationSpeed); // Simulate customer interaction speed
                    // Check if vendors are active and if the tickets to retrieve exceed the remaining tickets
                    if (Main.activeVendors && ticketsToRetrieve > Vendor.getTotalTicketLimit() - Vendor.getTotalTicketsAdded()) {
                        continue;
                    } else if (!Main.activeVendors && ticketsToRetrieve >= ticketPool.getTicketCount()) {
                        finalTransaction = true; // Set final transaction flag if no more tickets are available
                    }
                    ticketPool.retrieveTickets(customerName, ticketsToRetrieve, finalTransaction); // Retrieve tickets from the pool

                    // Print final message and stop the simulation if the final transaction has occurred
                    if (finalTransaction && !messagePrinted) {
                        console.appendOutput("--- Customer " + customerName + " --- Retrieved the last available tickets. \n\s\s\sEnding simulation.");
                        Main.stopSimulation(false);
                        if (Console.saveFile) console.saveJsonFile();
                        messagePrinted = true;
                        break;
                    }
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Handle thread interruption
            }
        }
    }
}