public class VIPCustomer extends Customer {
    private static int vipCustomerCount = 0;

    public VIPCustomer(int maxTicketsToRetrieve, TicketPool ticketPool, Console console, int simulationSpeed) {
        super(maxTicketsToRetrieve, ticketPool, console, simulationSpeed);
        setCustomerName("VIP-C" + vipCustomerCount++);
    }
}