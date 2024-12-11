import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

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

    public Vendor(int vendorReleaseRate, TicketPool ticketPool, Console console, int simulationSpeed) {
        this.vendorName = "V" + vendorCount++;
        this.vendorReleaseRate = vendorReleaseRate;
        this.ticketPool = ticketPool;
        this.console = console;
        this.simulationSpeed = simulationSpeed;
    }

    public static void setTotalTicketLimit(int totalTicketLimit) {
        Vendor.totalTicketLimit = totalTicketLimit;
    }

    public static void setMessagePrinted(boolean messagePrinted) {
        Vendor.messagePrinted = messagePrinted;
    }

    public static void setTotalTicketsAdded(int totalTicketsAdded) {
        Vendor.totalTicketsAdded = totalTicketsAdded;
    }

    public static int getTotalTicketLimit() {
        return totalTicketLimit;
    }

    public static int getTotalTicketsAdded() {
        return totalTicketsAdded;
    }

    public static void setVendorCount(int vendorCount) {
        Vendor.vendorCount = vendorCount;
    }

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