/**
 * The VIPCustomer class represents a VIP customer that retrieves tickets from a ticket pool.
 * Extends the Customer class to inherit its properties and methods.
 */
public class VIPCustomer extends Customer {
    private static int vipCustomerCount = 0;

    /**
     * Constructs a VIPCustomer with the specified retrieval rate, ticket pool, console, and simulation speed.
     *
     * @param maxTicketsToRetrieve the maximum number of tickets the VIP customer can retrieve at a time
     * @param ticketPool the ticket pool from which the VIP customer retrieves tickets
     * @param console the console for logging output
     * @param simulationSpeed the speed of the simulation in milliseconds
     */
    public VIPCustomer(int maxTicketsToRetrieve, TicketPool ticketPool, Console console, int simulationSpeed) {
        super(maxTicketsToRetrieve, ticketPool, console, simulationSpeed);
        setCustomerName("VIP-C" + vipCustomerCount++);
    }
}