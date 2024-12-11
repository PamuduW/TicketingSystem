import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Scanner;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    private static ThreadPoolExecutor threadPoolExecutor;
    private static final ReentrantLock lock = new ReentrantLock();
    private static int totalTickets, vendorReleaseRate, customerRetrievalRate, maxTicketCapacity;
    private static boolean isRunning = false;
    public static boolean activeVendors;
    public static Console console;
    public static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("\n--- Welcome to the ticketing simulation ---\n\nSearching for a Config file...");
        File configFile = new File("Logs/config.json");
        boolean fileExists = configFile.exists();

        while (true) {
            if (fileExists) {
                System.out.println("Config file found");
                try (FileReader reader = new FileReader(configFile)) {
                    Config config = new Gson().fromJson(reader, Config.class);
                    totalTickets = config.getTotalTickets();
                    vendorReleaseRate = config.getVendorReleaseRate();
                    customerRetrievalRate = config.getCustomerRetrievalRate();
                    maxTicketCapacity = config.getMaxTicketCapacity();
                    break;
                } catch (Exception e) {
                    System.out.println("An error has occurred with the file reading");
                    fileExists = false;
                }
            } else {
                System.out.println("Config file not found or corrupted");
                setConfig();
                break;
            }
        }

        System.out.printf("\n--- Current Config ---\nTotal Number of Tickets: %d\nMaximum Ticket Release Rate: %d\nMaximum Customer Retrieval Rate: %d\nMaximum Ticket Capacity: %d\n",
                totalTickets, vendorReleaseRate, customerRetrievalRate, maxTicketCapacity);
        console = new Console();

        while (true) {
            System.out.print("""
                    \n--- Main Menu ---
                    1. Start
                    2. Stop
                    3. Edit Config File
                    4. Quit
                    -----------------
                    Choose an option:\s""");
            try {
                switch (Integer.parseInt(scanner.nextLine())) {
                    case 1 -> startSimulation();
                    case 2 -> stopSimulation(true);
                    case 3 -> setConfig();
                    case 4 -> {
                        console.closeFrame();
                        return;
                    }
                    default -> throw new Exception("Wrong_input");
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Try again.");
            }
        }
    }

    private static void setConfig() {
        while (true) {
            System.out.println("\n--- Setting the Config File ---");
            totalTickets = getInput("Enter the Total Number of Tickets: ");
            vendorReleaseRate = getInput("Enter Maximum Ticket Release Rate: ");
            customerRetrievalRate = getInput("Enter Maximum Customer Retrieval Rate: ");
            maxTicketCapacity = getInput("Enter Maximum Ticket Capacity: ");
            try {
                new File("Logs").mkdir();
                Config config = new Config(totalTickets, vendorReleaseRate, customerRetrievalRate, maxTicketCapacity);
                try (FileWriter writer = new FileWriter("Logs/config.json")) {
                    new Gson().toJson(config, writer);
                }
                System.out.println("Successfully wrote the config information to the file");
                break;
            } catch (Exception e) {
                System.out.println("An error has occurred with the file saving");
            }
        }
    }

    private static int getInput(String message) {
        while (true) {
            try {
                System.out.print(message);
                int config = Integer.parseInt(scanner.nextLine());
                if (config > 0) return config;
                throw new Exception("Wrong_input");
            } catch (Exception e) {
                System.out.println("Invalid input. Try again.");
            }
        }
    }

    private static void startSimulation() {
        if (isRunning) {
            System.out.println("The simulation is already running.");
            return;
        }
        System.out.println("\n--- Simulation Configuration ---");
        int noOfVendors = getInput("Enter the Number of Vendors simulated: ");
        int noOfVIPCustomers = getInput("Enter the Number of VIP Customers simulated: ");
        int noOfCustomers = getInput("Enter the Number of Customers simulated: ");
        int vendorSpeed = getInput("Enter the Vendor interaction Speed (in ms): ");
        int customerSpeed = getInput("Enter the Customer interaction Speed (in ms): ");

        if (console.getSaveFilePermission()) {
            console.getSaveFileName();
        }

        activeVendors = true;
        isRunning = true;
        Vendor.setTotalTicketLimit(totalTickets);
        TicketPool ticketPool = new TicketPool(maxTicketCapacity, console);
        Vendor.setMessagePrinted(false);
        Vendor.setTotalTicketsAdded(0);
        Vendor.setVendorCount(0);
        Customer.setCustomerCount(0);
        console.setFrameVisibility();
        Customer.setFinalTransaction(false);
        Customer.setMessagePrinted(false);

        threadPoolExecutor = new ThreadPoolExecutor(
                noOfVendors + noOfVIPCustomers + noOfCustomers,
                noOfVendors + noOfVIPCustomers + noOfCustomers,
                0L, TimeUnit.MILLISECONDS,
                new PriorityBlockingQueue<>()
        );

        for (int i = 0; i < noOfVendors; i++) {
            threadPoolExecutor.submit(new Vendor(vendorReleaseRate, ticketPool, console, vendorSpeed));
        }
        for (int i = 0; i < noOfCustomers; i++) {
            threadPoolExecutor.submit(new CustomerTask(new Customer(customerRetrievalRate, ticketPool, console, customerSpeed), false));
        }
        for (int i = 0; i < noOfVIPCustomers; i++) {
            threadPoolExecutor.submit(new CustomerTask(new VIPCustomer(customerRetrievalRate, ticketPool, console, customerSpeed), true));
        }
        console.appendOutput("Simulation started.");
        System.out.println("Simulation started.");
    }

    public static void stopSimulation(boolean message) {
        lock.lock();
        try {
            if (!isRunning) {
                System.out.println("Simulation is not running.");
                return;
            }
            threadPoolExecutor.shutdownNow();
            isRunning = false;
            if (message) {
                System.out.println("Simulation stopped by the user.");
                console.appendOutput("Simulation stopped by the user.");
            }
        } finally {
            lock.unlock();
        }
    }
}