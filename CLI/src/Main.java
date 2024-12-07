import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Main {
    private static ThreadPoolExecutor threadPoolExecutor;
    private static final ReentrantLock lock = new ReentrantLock();
    private static int totalTickets;
    private static int vendorReleaseRate;
    private static int customerRetrievalRate;
    private static int maxTicketCapacity;
    private static boolean isRunning = false;
    public static boolean activeVendors;
    public static Console console;
    public static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("\n--- Welcome to the ticketing simulation ---\n\nSearching for a Config file...");

        // Check if the config file exists
        File configFile = new File("Logs/config.json");
        boolean fileExists = configFile.exists();

        while (true) {
            if (fileExists) {
                System.out.println("Config file found");
                try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
                    // Read and parse the config file
                    reader.readLine();
                    totalTickets = Integer.parseInt(reader.readLine().split(":")[1].trim().split(",")[0]);
                    vendorReleaseRate = Integer.parseInt(reader.readLine().split(":")[1].trim().split(",")[0]);
                    customerRetrievalRate = Integer.parseInt(reader.readLine().split(":")[1].trim().split(",")[0]);
                    maxTicketCapacity = Integer.parseInt(reader.readLine().split(":")[1].trim().split(",")[0]);
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
        System.out.println("\n--- Current Config ---");
        System.out.println("Total Number of Tickets : " + totalTickets);
        System.out.println("Maximum Ticket Release Rate : " + vendorReleaseRate);
        System.out.println("Maximum Customer Retrieval Rate : " + customerRetrievalRate);
        System.out.println("Maximum Ticket Capacity : " + maxTicketCapacity);
        console = new Console();

        while (true) {
            System.out.print("""
                    \n--- Main Menu ---
                    1. Start
                    2. Stop
                    3. Edit Config File
                    4. Quit
                    -----------------
                    Choose an option :\s""");
            try {
                switch (Integer.parseInt(scanner.nextLine())) {
                    case 1 -> {
                        console = new Console();
                        startSimulation(totalTickets, vendorReleaseRate, customerRetrievalRate, maxTicketCapacity, console);
                    }
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

    // Method to set the configuration
    private static void setConfig() {
        while (true) {
            System.out.println("\n--- Setting the Config File ---");
            totalTickets = getInput("Enter Total Number of Tickets : ");
            vendorReleaseRate = getInput("Enter Maximum Ticket Release Rate : ");
            customerRetrievalRate = getInput("Enter Maximum Customer Retrieval Rate : ");
            maxTicketCapacity = getInput("Enter Maximum Ticket Capacity : ");
            try {
                new File("Logs").mkdir();
                FileWriter textFileWriter = new FileWriter("Logs/config.json");
                textFileWriter.write("{\n\"totalTickets\" : " + totalTickets
                        + ",\n\"vendorReleaseRate\" : " + vendorReleaseRate
                        + ",\n\"customerRetrievalRate\" : " + customerRetrievalRate
                        + ",\n\"maxTicketCapacity\" : " + maxTicketCapacity + "\n}");
                textFileWriter.close();
                System.out.println("Successfully wrote the config information to the file");
                break;
            } catch (Exception e) {
                System.out.println("An error has occurred with the file saving");
            }
        }
    }

    // Method to get user input for configuration
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

    // Method to start the simulation
    private static void startSimulation(int totalTickets, int vendorReleaseRate, int customerRetrievalRate, int maxTicketCapacity, Console console) {
        if (isRunning) {
            System.out.println("Simulation is already running.");
            return;
        }
        System.out.println("\n--- Simulation Configuration ---");
        int noOfVendors = getInput("Enter Number of Vendors simulated : ");
        int noOfVIPCustomers = getInput("Enter Number of VIP Customers simulated : ");
        int noOfCustomers = getInput("Enter Number of Customers simulated : ");
        int vendorSpeed = getInput("Enter Vendor interaction Speed (in ms) : ");
        int customerSpeed = getInput("Enter Customer interaction Speed (in ms) : ");

        // Check if the user wants to save the simulation log
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
        System.out.println("Simulation started.");
    }

    // Method to stop the simulation
    public static void stopSimulation(boolean message) {
        lock.lock();
        try {
            if (!isRunning) {
                System.out.println("Simulation is not running.");
                return;
            }

            // Interrupt all vendor and customer threads
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