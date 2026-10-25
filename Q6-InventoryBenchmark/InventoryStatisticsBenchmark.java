
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

public class InventoryStatisticsBenchmark {

    private static final int THREADS = 10;
    private static final int OPERATIONS_PER_THREAD = 5000;
    private static final int ROUNDS = 5;

    private static double runBenchmark(boolean concurrent)
            throws Exception {

        InventoryCatalog catalog = concurrent
                ? InventoryCatalog.concurrentCatalog()
                : InventoryCatalog.synchronizedCatalog();

        ExecutorService executor =
                Executors.newFixedThreadPool(THREADS);

        List<Future<InventoryWorker.WorkerResult>> tasks =
                new ArrayList<>();

        long start = System.nanoTime();

        try {

            for (int i = 0; i < THREADS; i++) {
                tasks.add(executor.submit(
                    new InventoryWorker(
                        catalog, i, OPERATIONS_PER_THREAD
                    )
                ));
            }

            executor.shutdown();

            int reads = 0;
            int updates = 0;

            for (Future<InventoryWorker.WorkerResult> task
                    : tasks) {

                InventoryWorker.WorkerResult result =
                        task.get(30, TimeUnit.SECONDS);

                if (result.getReads() != 2500 ||
                    result.getUpdates() != 2500) {

                    throw new AssertionError(
                        "Worker operation count mismatch."
                    );
                }

                reads += result.getReads();
                updates += result.getUpdates();
            }

            if (!executor.awaitTermination(
                    30, TimeUnit.SECONDS)) {

                throw new IllegalStateException(
                    "Inventory workers did not terminate."
                );
            }

            double elapsedMs =
                    (System.nanoTime() - start) / 1_000_000.0;

            if (reads != 25000 ||
                updates != 25000 ||
                catalog.getProductCount() != 100 ||
                catalog.getTotalStock() != 125000) {

                throw new AssertionError(
                    "Inventory verification failed."
                );
            }

            for (int i = 0; i < 100; i++) {
                if (catalog.getStock(i) != 1250) {
                    throw new AssertionError(
                        "Incorrect product stock: " + i
                    );
                }
            }

            return elapsedMs;

        } finally {
            executor.shutdownNow();
        }
    }

    private static void printStatistics(
            String name, double[] times) {

        double[] sorted = times.clone();
        Arrays.sort(sorted);

        double total = 0.0;

        for (double time : times) {
            total += time;
        }

        double average = total / times.length;
        double median = sorted[sorted.length / 2];

        System.out.println("\n--- " + name + " ---");

        System.out.printf(
            "Minimum Time: %.3f ms%n", sorted[0]
        );

        System.out.printf(
            "Maximum Time: %.3f ms%n",
            sorted[sorted.length - 1]
        );

        System.out.printf(
            "Average Time: %.3f ms%n", average
        );

        System.out.printf(
            "Median Time: %.3f ms%n", median
        );
    }

    public static void main(String[] args)
            throws Exception {

        System.out.println(
            "===== Inventory Benchmark Statistics ====="
        );

        System.out.println(
            "Student: Sultan Majeed | Reg ID: 247187"
        );

        System.out.println("Worker Threads: " + THREADS);
        System.out.println("Products: 100");
        System.out.println("Operations Per Run: 50000");

        // Warm up the JVM and both map implementations
        for (int i = 0; i < 2; i++) {
            runBenchmark(false);
            runBenchmark(true);
        }

        double[] synchronizedTimes = new double[ROUNDS];
        double[] concurrentTimes = new double[ROUNDS];

        System.out.println("\n===== Measured Rounds =====");

        for (int round = 0; round < ROUNDS; round++) {

            // Alternate benchmark execution order
            if (round % 2 == 0) {
                synchronizedTimes[round] =
                        runBenchmark(false);

                concurrentTimes[round] =
                        runBenchmark(true);
            } else {
                concurrentTimes[round] =
                        runBenchmark(true);

                synchronizedTimes[round] =
                        runBenchmark(false);
            }

            System.out.printf(
                "Round %d: Synchronized %.3f ms"
                + " | Concurrent %.3f ms%n",
                round + 1,
                synchronizedTimes[round],
                concurrentTimes[round]
            );
        }

        System.out.println("\n===== Statistical Summary =====");

        printStatistics(
            "Collections.synchronizedMap",
            synchronizedTimes
        );

        printStatistics(
            "ConcurrentHashMap",
            concurrentTimes
        );

        double synchronizedAverage = 0.0;
        double concurrentAverage = 0.0;

        for (int i = 0; i < ROUNDS; i++) {
            synchronizedAverage += synchronizedTimes[i];
            concurrentAverage += concurrentTimes[i];
        }

        synchronizedAverage /= ROUNDS;
        concurrentAverage /= ROUNDS;

        double speedup =
                synchronizedAverage / concurrentAverage;

        System.out.println("\n===== Final Result =====");

        System.out.printf(
            "ConcurrentHashMap Speedup: %.2fx%n",
            speedup
        );

        System.out.println(
            "Faster Average: " +
            (speedup > 1.0
                ? "ConcurrentHashMap"
                : "Collections.synchronizedMap")
        );

        System.out.println(
            "All Inventory Checks: PASSED"
        );

        System.out.println(
            "\n===== Statistics Benchmark Completed ====="
        );
    }
}
