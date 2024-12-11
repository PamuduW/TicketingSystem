/**
 * The CustomerTask class represents a task for a customer, which can be either a regular customer or a VIP customer.
 * It implements the Runnable and Comparable interfaces to allow execution in a separate thread and comparison based on VIP status.
 */
public class CustomerTask extends Console implements Runnable, Comparable<CustomerTask> {
    private final Runnable task;
    private final boolean isVIP;

    /**
     * Constructs a CustomerTask with the specified task and VIP status.
     *
     * @param task the task to be executed
     * @param isVIP true if the customer is a VIP, false otherwise
     */
    public CustomerTask(Runnable task, boolean isVIP) {
        this.task = task;
        this.isVIP = isVIP;
    }

    /**
     * Executes the task.
     */
    @Override
    public void run() {
        task.run();
    }

    /**
     * Compares this CustomerTask with another based on VIP status.
     *
     * @param other the other CustomerTask to compare to
     * @return a negative integer, zero, or a positive integer as this CustomerTask is less than, equal to, or greater than the specified CustomerTask
     */
    @Override
    public int compareTo(CustomerTask other) {
        return Boolean.compare(other.isVIP, this.isVIP);
    }
}