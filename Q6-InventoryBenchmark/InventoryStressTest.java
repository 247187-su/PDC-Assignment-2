
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class InventoryStressTest {

    private static final int THREADS = 20;
    private static final int OPERATIONS_PER_THREAD = 10000;

    private static int passed = 0;
    private static int failed = 0;

    private static void check(String name, boolean condition) {

        if (condition) {
            System.out.println("[PASS] " + name);
            passed++;
        } else {
            System.out.println("[FAIL] " + name);
            failed++;
        }
    }

    private static void testCatalog(
            String label, InventoryCatalog catalog)
            throws Exception {

        System.out.println("\n--- Testing " + label + " ---");

        ExecutorService executor =
                Executors.newFixedThreadPool(THREADS);

        List<Future<InventoryWorker.WorkerResult>> tasks =
                new ArrayList<>();

        long startTime = System.nanoTime();

        int reads = 0;
        int updates = 0;
        boolean workersValid = true;

        try {

            // Submit 20 inventory workers
            for (int i = 0; i < THREADS; i++) {

                tasks.add(executor.submit(
                    new InventoryWorker(
                        catalog, i, OPERATIONS_PER_THREAD
                    )
                ));
            }

            executor.shutdown();

            // Collect and verify every worker's result
            for (Future<InventoryWorker.WorkerResult> task
                    : tasks) {

                InventoryWorker.WorkerResult result =
                        task.get(60, TimeUnit.SECONDS);

                reads += result.getReads();
                updates += result.getUpdates();

                if (result.getReads() != 5000 ||
                    result.getUpdates() != 5000) {

                    workersValid = false;
                }
            }

            if (!executor.awaitTermination(
                    60, TimeUnit.SECONDS)) {

                throw new IllegalStateException(
                    "Stress test workers timed out."
                );
            }

        } finally {
            executor.shutdownNow();
        }

        double elapsedMs =
                (System.nanoTime() - startTime)
                / 1_000_000.0;

        // Every product receives exactly 1000 increments
        boolean productStockValid = true;

        for (int i = 0; i < 100; i++) {

            if (catalog.getStock(i) != 2000) {

                productStockValid = false;
                break;
            }
        }

        check(label + " - All workers completed",
                workersValid);

        check(label + " - Correct read count",
                reads == 100000);

        check(label + " - Correct update count",
                updates == 100000);

        check(label + " - Total operations",
                reads + updates == 200000);

        check(label + " - Product count",
                catalog.getProductCount() == 100);

        check(label + " - Total stock preserved",
                catalog.getTotalStock() == 200000);

        check(label + " - Every product stock correct",
                productStockValid);

        System.out.println("Worker Threads: " + THREADS);
        System.out.println("Total Operations: " + (reads + updates));
        System.out.println("Final Stock: " + catalog.getTotalStock());

        System.out.printf(
            "Execution Time: %.3f ms%n", elapsedMs
        );
    }

    public static void main(String[] args) throws Exception {

        System.out.println(
            "===== Inventory Concurrency Stress Tests ====="
        );

        System.out.println(
            "Student: Sultan Majeed | Reg ID: 247187"
        );

        testCatalog(
            "SynchronizedMap",
            InventoryCatalog.synchronizedCatalog()
        );

        testCatalog(
            "ConcurrentHashMap",
            InventoryCatalog.concurrentCatalog()
        );

        System.out.println("\n===== Test Summary =====");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);

        if (failed > 0) {
            throw new AssertionError(
                "Inventory stress tests failed."
            );
        }

        System.out.println("RESULT: ALL TESTS PASSED");
    }
}
