import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class Customer implements Runnable {
    private String customerName;
    private final int customerRetrievalRate;
    private final int simulationSpeed;
    private final TicketPool ticketPool;
    private final Console console;
    private static int customerCount = 0;
    private static boolean messagePrinted = false;
    private static boolean finalTransaction = false;
    private static final ReentrantLock lock = new ReentrantLock();

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

    public static void setCustomerCount(int customerCount) {
        Customer.customerCount = customerCount;
    }

    public static void setFinalTransaction(boolean finalTransaction) {
        Customer.finalTransaction = finalTransaction;
    }

    public static void setMessagePrinted(boolean messagePrinted) {
        Customer.messagePrinted = messagePrinted;
    }

    @Override
    public void run() {
        Random random = new Random();
        while (!finalTransaction) {
            try {
                Thread.sleep(simulationSpeed);
                int ticketsToRetrieve = random.nextInt(customerRetrievalRate) + 1;

                lock.lock();
                try {
                    Thread.sleep(simulationSpeed);
                    if (Main.activeVendors && ticketsToRetrieve > Vendor.getTotalTicketLimit() - Vendor.getTotalTicketsAdded()) {
                        continue;
                    } else if (!Main.activeVendors && ticketsToRetrieve >= ticketPool.getTicketCount()) {
                        finalTransaction = true;
                    }
                    ticketPool.retrieveTickets(customerName, ticketsToRetrieve, finalTransaction);

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
                Thread.currentThread().interrupt();
            }
        }
    }
}