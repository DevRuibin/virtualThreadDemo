import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Virtual
 * CPU Load: 94.90445859872611%, Virtual Threads: 0
    count:188688
 * 
 * Traditional
 * CPU Load: 92.34449760765551%, Traditional Threads: 10
CPU Load: 100.0%, Traditional Threads: 11
count:11
CPU usage has reached 60%. Stopping thread creation.
 */


public class ThreadLoadTest {

    public static long count = 0;

    public static void main(String[] args) {
        if (args.length == 0 || (!args[0].equalsIgnoreCase("traditional") && !args[0].equalsIgnoreCase("virtual"))) {
            System.out.println("Usage: java ThreadLoadTest <traditional|virtual>");
            return;
        }

        String mode = args[0].toLowerCase();

        

        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();

        double cpuLoad = getSystemCpuLoad(osBean) * 100;
        System.out.println("Inital Cpu usage: " + cpuLoad);

        if (mode.equals("traditional")) {
            testTraditionalThreads(osBean);
        } else if (mode.equals("virtual")) {
            testVirtualThreads(osBean);
        }
    }

    // Test using Traditional Threads
    private static void testTraditionalThreads(OperatingSystemMXBean osBean) {
        List<Thread> threads = new ArrayList<>();

        // Monitor the CPU usage and start threads until CPU usage hits 60%
        while (true) {
            // Start a new thread that does some dummy work
            count++;
            Thread t = new Thread(() -> {
                while (true) {
                    Math.sin(Math.random());  // Simulate CPU work
                }
            });
            t.start();
            threads.add(t);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // Check CPU usage
            double cpuLoad = getSystemCpuLoad(osBean) * 100;

            // Print the current CPU load
            System.out.println("CPU Load: " + cpuLoad + "%, Traditional Threads: " + threads.size());

            // Stop if CPU load is 99% or more
            if (cpuLoad >= 99.0) {
                System.out.println("count:" + count);
                System.out.println("CPU usage has reached 60%. Stopping thread creation.");
                break;
            }
        }
    }

    // Test using Virtual Threads
    private static void testVirtualThreads(OperatingSystemMXBean osBean) {
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        List<Thread> threads = new ArrayList<>();

        // Monitor the CPU usage and start threads until CPU usage hits 60%
        while (true) {
            count++;
            // Start a new virtual thread that does some dummy work
            executor.submit(() -> {
                while (true) {
                    Math.sin(Math.random());  // Simulate CPU work
                }
            });

            // Check CPU usage
            double cpuLoad = getSystemCpuLoad(osBean) * 100;

            // Print the current CPU load
            System.out.println("CPU Load: " + cpuLoad + "%, Virtual Threads: " + threads.size());

            // Stop if CPU load is 99% or more
            if (cpuLoad >= 99.0) {
                System.out.println("count:" + count);
                System.out.println("CPU usage has reached 60%. Stopping thread creation.");
                break;
            }
        }

        executor.shutdown();  // Shutdown the virtual thread executor
    }

    // Retrieve the system CPU load (0.0 to 1.0)
    private static double getSystemCpuLoad(OperatingSystemMXBean osBean) {
        if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
            return ((com.sun.management.OperatingSystemMXBean) osBean).getCpuLoad();
        }
        throw new UnsupportedOperationException("CPU load monitoring is not supported on this JVM.");
    }
}