package com.backend.ticketing_System.sim;

/**
 * Simulates VIP customer behavior in the ticketing system.
 */
public class VIPCustomerSim extends CustomerSim {
    private static int vipCustomerCount = 0; // Counter to assign unique names to VIP customers

    /**
     * Constructs a new VIPCustomerSim with the specified maximum tickets to retrieve and event simulation.
     *
     * @param maxTicketsToRetrieve the maximum number of tickets the VIP customer can retrieve.
     * @param eventSim the event simulation instance.
     */
    public VIPCustomerSim(int maxTicketsToRetrieve, EventSim eventSim) {
        super(maxTicketsToRetrieve, eventSim);
        setCustomerName("VIP-C" + vipCustomerCount++);
    }
}