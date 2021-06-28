package threading;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Pool {
    ThreadPoolExecutor executor;
    private int maxThreads;

    public Pool() {
        try {
            String file = System.getProperty("user.dir") + "/config.conf";
            System.out.println(file);
            java.util.List<String> lines = new ArrayList<String>();
            Pattern pattern = Pattern.compile("\\[(.+)\\]");
            Matcher matcher;
            Stream<String> stream = null;
            stream = Files.lines(Paths.get(file));
            lines = stream.filter(line -> !line.startsWith("#")).collect(Collectors.toList());
            for (int i = 0; i < lines.size(); i++) {
                if (lines.get(i).startsWith("maxthreads")) {
                    matcher = pattern.matcher(lines.get(i));
                    if (matcher.find())
                        maxThreads = Integer.parseInt(matcher.group(1));
                    else
                        maxThreads = 50;
                }
            }
        } catch (IOException e) {
            maxThreads = 50;
        }
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

    public int getMaxThreads() {
        return maxThreads;
    }

    public void setMaxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
        executor.setMaximumPoolSize(maxThreads);
    }

}
