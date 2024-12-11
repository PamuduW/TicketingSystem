/**
 * The Config class represents the configuration settings for the ticketing simulation.
 * It includes the total number of tickets, vendor release rate, customer retrieval rate, and maximum ticket capacity.
 */
class Config {
    private final int totalTickets;
    private final int vendorReleaseRate;
    private final int customerRetrievalRate;
    private final int maxTicketCapacity;

    /**
     * Constructs a Config object with the specified parameters.
     *
     * @param totalTickets the total number of tickets available
     * @param vendorReleaseRate the rate at which vendors release tickets
     * @param customerRetrievalRate the rate at which customers retrieve tickets
     * @param maxTicketCapacity the maximum capacity of the ticket pool
     */
    public Config(int totalTickets, int vendorReleaseRate, int customerRetrievalRate, int maxTicketCapacity) {
        this.totalTickets = totalTickets;
        this.vendorReleaseRate = vendorReleaseRate;
        this.customerRetrievalRate = customerRetrievalRate;
        this.maxTicketCapacity = maxTicketCapacity;
    }

    /**
     * Gets the total number of tickets.
     *
     * @return the total number of tickets
     */
    public int getTotalTickets() {
        return totalTickets;
    }

    /**
     * Gets the vendor release rate.
     *
     * @return the vendor release rate
     */
    public int getVendorReleaseRate() {
        return vendorReleaseRate;
    }

    /**
     * Gets the customer retrieval rate.
     *
     * @return the customer retrieval rate
     */
    public int getCustomerRetrievalRate() {
        return customerRetrievalRate;
    }

    /**
     * Gets the maximum ticket capacity.
     *
     * @return the maximum ticket capacity
     */
    public int getMaxTicketCapacity() {
        return maxTicketCapacity;
    }
}