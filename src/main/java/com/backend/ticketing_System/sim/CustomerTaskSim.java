package com.backend.ticketing_System.sim;

import lombok.NonNull;

public class CustomerTaskSim implements Runnable, Comparable<CustomerTaskSim> {
    private final Runnable task;
    private final boolean isVIP;

    public CustomerTaskSim(Runnable task, boolean isVIP) {
        this.task = task;
        this.isVIP = isVIP;
    }

    @Override
    public void run() {
        task.run();
    }

    @Override
    public int compareTo(@NonNull CustomerTaskSim other) {
        return Boolean.compare(other.isVIP, this.isVIP);
    }
}
