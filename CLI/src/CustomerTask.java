public class CustomerTask implements Runnable, Comparable<CustomerTask> {
    private final Runnable task;
    private final boolean isVIP;

    public CustomerTask(Runnable task, boolean isVIP) {
        this.task = task;
        this.isVIP = isVIP;
    }

    @Override
    public void run() {
        task.run();
    }

    @Override
    public int compareTo(CustomerTask other) {
        return Boolean.compare(other.isVIP, this.isVIP);
    }
}