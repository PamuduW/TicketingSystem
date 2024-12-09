class Config {
    private final int totalTickets;
    private final int vendorReleaseRate;
    private final int customerRetrievalRate;
    private final int maxTicketCapacity;

    public Config(int totalTickets, int vendorReleaseRate, int customerRetrievalRate, int maxTicketCapacity) {
        this.totalTickets = totalTickets;
        this.vendorReleaseRate = vendorReleaseRate;
        this.customerRetrievalRate = customerRetrievalRate;
        this.maxTicketCapacity = maxTicketCapacity;
    }

    public int getTotalTickets() {
        return totalTickets;
    }

    public int getVendorReleaseRate() {
        return vendorReleaseRate;
    }

    public int getCustomerRetrievalRate() {
        return customerRetrievalRate;
    }

    public int getMaxTicketCapacity() {
        return maxTicketCapacity;
    }
}