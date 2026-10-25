
import java.util.concurrent.Callable;

public class InventoryWorker
        implements Callable<InventoryWorker.WorkerResult> {

    private final InventoryCatalog catalog;
    private final int workerId;
    private final int operationCount;

    public InventoryWorker(
            InventoryCatalog catalog,
            int workerId,
            int operationCount) {

        if (catalog == null) {
            throw new IllegalArgumentException(
                "Inventory catalog cannot be null."
            );
        }

        if (workerId < 0) {
            throw new IllegalArgumentException(
                "Worker ID cannot be negative."
            );
        }

        if (operationCount <= 0 || operationCount % 2 != 0) {
            throw new IllegalArgumentException(
                "Operation count must be positive and even."
            );
        }

        this.catalog = catalog;
        this.workerId = workerId;
        this.operationCount = operationCount;
    }

    @Override
    public WorkerResult call() {

        int reads = 0;
        int updates = 0;
        long checksum = 0;

        for (int i = 0; i < operationCount; i++) {

            // Distribute operations across 100 products
            int productId =
                (workerId * 13 + i / 2)
                % InventoryCatalog.PRODUCT_COUNT;

            if (i % 2 == 0) {

                // Read product stock
                int stock = catalog.getStock(productId);

                checksum += stock;
                reads++;

            } else {

                // Atomically increase product stock
                catalog.addStock(productId, 1);

                updates++;
            }
        }

        return new WorkerResult(
            reads, updates, checksum
        );
    }

    public static final class WorkerResult {

        private final int reads;
        private final int updates;
        private final long checksum;

        public WorkerResult(
                int reads, int updates, long checksum) {

            this.reads = reads;
            this.updates = updates;
            this.checksum = checksum;
        }

        public int getReads() {
            return reads;
        }

        public int getUpdates() {
            return updates;
        }

        public long getChecksum() {
            return checksum;
        }
    }
}
