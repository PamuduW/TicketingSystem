package com.backend.ticketing_System.sim;

import lombok.NonNull;

/**
 * Simulates a customer task in the ticketing system.
 */
public class CustomerTaskSim implements Runnable, Comparable<CustomerTaskSim> {
    private final Runnable task;
    private final boolean isVIP;

    /**
     * Constructs a new CustomerTaskSim with the specified task and VIP status.
     *
     * @param task the task to be executed.
     * @param isVIP whether the customer is a VIP.
     */
    public CustomerTaskSim(Runnable task, boolean isVIP) {
        this.task = task;
        this.isVIP = isVIP;
    }

    /**
     * Runs the customer task.
     */
    @Override
    public void run() {
        task.run();
    }

    /**
     * Compares this CustomerTaskSim with another based on VIP status.
     *
     * @param other the other CustomerTaskSim to compare to.
     * @return a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(@NonNull CustomerTaskSim other) {
        return Boolean.compare(other.isVIP, this.isVIP);
    }
}