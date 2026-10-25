
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class SynchronizedMapBenchmark {

    public static void main(String[] args) throws Exception {

        System.out.println(
            "===== SynchronizedMap Inventory Benchmark ====="
        );

        System.out.println(
            "Student: Sultan Majeed | Reg ID: 247187"
        );

        final int THREADS = 10;
        final int OPERATIONS_PER_THREAD = 5000;

        // Create synchronized inventory
        InventoryCatalog catalog =
                InventoryCatalog.synchronizedCatalog();

        ExecutorService executor =
                Executors.newFixedThreadPool(THREADS);

        List<Future<InventoryWorker.WorkerResult>> futures =
                new ArrayList<>();

        int totalReads = 0;
        int totalUpdates = 0;
        long readChecksum = 0;

        System.out.println("\n===== Configuration =====");

        System.out.println("Map Type: " + catalog.getMapType());
        System.out.println("Products: " + catalog.getProductCount());
        System.out.println("Worker Threads: " + THREADS);
        System.out.println(
            "Operations Per Thread: " + OPERATIONS_PER_THREAD
        );

        long startTime = System.nanoTime();

        try {

            // Submit 10 concurrent inventory workers
            for (int i = 0; i < THREADS; i++) {

                futures.add(
                    executor.submit(
                        new InventoryWorker(
                            catalog, i, OPERATIONS_PER_THREAD
                        )
                    )
                );
            }

            // Stop accepting new tasks
            executor.shutdown();

            // Collect results from each worker
            for (Future<InventoryWorker.WorkerResult> future
                    : futures) {

                InventoryWorker.WorkerResult result =
                        future.get();

                totalReads += result.getReads();
                totalUpdates += result.getUpdates();
                readChecksum += result.getChecksum();
            }

            if (!executor.awaitTermination(
                    30, TimeUnit.SECONDS)) {

                throw new IllegalStateException(
                    "Inventory workers did not finish."
                );
            }

        } finally {
            executor.shutdownNow();
        }

        long endTime = System.nanoTime();

        double elapsedMs =
                (endTime - startTime) / 1_000_000.0;

        int expectedOperations =
                THREADS * OPERATIONS_PER_THREAD;

        long expectedStock =
                (long) InventoryCatalog.PRODUCT_COUNT
                * InventoryCatalog.INITIAL_STOCK
                + totalUpdates;

        long actualStock = catalog.getTotalStock();

        // Verify every product received correct updates
        boolean uniformStock = true;

        for (int productId = 0;
                productId < InventoryCatalog.PRODUCT_COUNT;
                productId++) {

            if (catalog.getStock(productId) != 1250) {
                uniformStock = false;
                break;
            }
        }

        boolean passed =
                totalReads == 25000 &&
                totalUpdates == 25000 &&
                totalReads + totalUpdates == expectedOperations &&
                actualStock == expectedStock &&
                uniformStock;

        System.out.println("\n===== Benchmark Results =====");

        System.out.println(
            "Total Operations: " + expectedOperations
        );

        System.out.println("Read Operations: " + totalReads);
        System.out.println("Update Operations: " + totalUpdates);

        System.out.println("Expected Total Stock: " + expectedStock);
        System.out.println("Actual Total Stock: " + actualStock);

        System.out.println(
            "Uniform Product Stock: " +
            (uniformStock ? "YES" : "NO")
        );

        System.out.println("Read Checksum: " + readChecksum);

        System.out.printf(
            "Execution Time: %.3f ms%n", elapsedMs
        );

        System.out.println(
            "Verification: " + (passed ? "PASSED" : "FAILED")
        );

        if (!passed) {
            throw new AssertionError(
                "SynchronizedMap inventory verification failed."
            );
        }

        System.out.println(
            "\n===== Benchmark Completed ====="
        );
    }
}
