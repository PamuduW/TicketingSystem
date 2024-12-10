package com.backend.ticketing_System.sim;

public class VIPCustomerSim extends CustomerSim {
    private static int vipCustomerCount = 0; // Counter to assign unique names to VIP customers

    public VIPCustomerSim(int maxTicketsToRetrieve, EventSim eventSim) {
        super(maxTicketsToRetrieve, eventSim);
        setCustomerName("VIP-C" + vipCustomerCount++);
    }
}
