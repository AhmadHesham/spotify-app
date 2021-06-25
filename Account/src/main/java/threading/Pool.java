package threading;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

public class Pool {
    ThreadPoolExecutor executor;
    private int maxThreads = 50;

    public Pool() {
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(maxThreads);
        RejectedExecution rejectionHandler = new RejectedExecution();
        executor.setRejectedExecutionHandler(rejectionHandler);
    }

    public void shutdown() {
        executor.shutdown();
    }

    public void execute(Runnable r) {
        executor.execute(r);
    }

    public void submit(Runnable r) {
        executor.submit(r);
    }

    public void setMaxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
        executor.setMaximumPoolSize(maxThreads);
    }

    public int getMaxThreads() {
        return maxThreads;
    }

}
