
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class InventoryComparisonApp {

    private static final int THREADS = 10;
    private static final int OPERATIONS_PER_THREAD = 5000;
    private static final int ROUNDS = 3;

    private static double runBenchmark(
            boolean concurrent) throws Exception {

        InventoryCatalog catalog = concurrent
                ? InventoryCatalog.concurrentCatalog()
                : InventoryCatalog.synchronizedCatalog();

        ExecutorService executor =
                Executors.newFixedThreadPool(THREADS);

        List<Future<InventoryWorker.WorkerResult>> futures =
                new ArrayList<>();

        long startTime = System.nanoTime();

        try {

            for (int i = 0; i < THREADS; i++) {

                futures.add(executor.submit(
                    new InventoryWorker(
                        catalog, i, OPERATIONS_PER_THREAD
                    )
                ));
            }

            executor.shutdown();

            int reads = 0;
            int updates = 0;

            for (Future<InventoryWorker.WorkerResult> future
                    : futures) {

                InventoryWorker.WorkerResult result =
                        future.get();

                reads += result.getReads();
                updates += result.getUpdates();
            }

            if (!executor.awaitTermination(
                    30, TimeUnit.SECONDS)) {

                throw new IllegalStateException(
                    "Inventory workers did not finish."
                );
            }

            long elapsedTime = System.nanoTime() - startTime;

            // Verify total inventory
            long expectedStock = 125000;
            long actualStock = catalog.getTotalStock();

            boolean valid =
                    reads == 25000 &&
                    updates == 25000 &&
                    actualStock == expectedStock &&
                    catalog.getProductCount() == 100;

            // Check every product's stock
            for (int i = 0; i < 100; i++) {

                if (catalog.getStock(i) != 1250) {
                    valid = false;
                    break;
                }
            }

            if (!valid) {
                throw new AssertionError(
                    "Inventory verification failed for "
                    + catalog.getMapType()
                );
            }

            return elapsedTime / 1_000_000.0;

        } finally {
            executor.shutdownNow();
        }
    }

    public static void main(String[] args) throws Exception {

        System.out.println(
            "===== Inventory Performance Comparison ====="
        );

        System.out.println(
            "Student: Sultan Majeed | Reg ID: 247187"
        );

        System.out.println("\nProducts: 100");
        System.out.println("Worker Threads: " + THREADS);
        System.out.println("Total Operations: 50000");
        System.out.println("Benchmark Rounds: " + ROUNDS);

        // Warm up both implementations
        runBenchmark(false);
        runBenchmark(true);

        double synchronizedTotal = 0;
        double concurrentTotal = 0;

        System.out.println("\n===== Benchmark Rounds =====");

        for (int round = 1; round <= ROUNDS; round++) {

            double synchronizedMs;
            double concurrentMs;

            // Alternate execution order
            if (round % 2 == 1) {

                synchronizedMs = runBenchmark(false);
                concurrentMs = runBenchmark(true);

            } else {

                concurrentMs = runBenchmark(true);
                synchronizedMs = runBenchmark(false);
            }

            synchronizedTotal += synchronizedMs;
            concurrentTotal += concurrentMs;

            System.out.printf(
                "Round %d: Synchronized = %.3f ms, "
                + "Concurrent = %.3f ms%n",
                round, synchronizedMs, concurrentMs
            );
        }

        double synchronizedAverage =
                synchronizedTotal / ROUNDS;

        double concurrentAverage =
                concurrentTotal / ROUNDS;

        double speedup =
                synchronizedAverage / concurrentAverage;

        System.out.println("\n===== Final Comparison =====");

        System.out.printf(
            "Average SynchronizedMap Time: %.3f ms%n",
            synchronizedAverage
        );

        System.out.printf(
            "Average ConcurrentHashMap Time: %.3f ms%n",
            concurrentAverage
        );

        System.out.printf(
            "ConcurrentHashMap Speedup: %.2fx%n",
            speedup
        );

        String faster = synchronizedAverage < concurrentAverage
                ? "Collections.synchronizedMap"
                : "ConcurrentHashMap";

        System.out.println("Faster In This Run: " + faster);

        System.out.println("Expected Stock: 125000");
        System.out.println("Actual Stock: 125000");

        System.out.println("Verification: PASSED");

        System.out.println(
            "\n===== Comparison Completed ====="
        );
    }
}
