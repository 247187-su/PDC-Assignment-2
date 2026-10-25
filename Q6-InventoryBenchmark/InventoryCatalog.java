
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InventoryCatalog {

    public static final int PRODUCT_COUNT = 100;
    public static final int INITIAL_STOCK = 1000;

    private final Map<String, Integer> inventory;
    private final String mapType;

    private InventoryCatalog(
            Map<String, Integer> inventory,
            String mapType) {

        this.inventory = inventory;
        this.mapType = mapType;

        // Initialize 100 products with equal stock
        for (int i = 0; i < PRODUCT_COUNT; i++) {
            inventory.put("Product-" + i, INITIAL_STOCK);
        }
    }

    // Create inventory backed by a synchronized HashMap
    public static InventoryCatalog synchronizedCatalog() {

        Map<String, Integer> map =
            Collections.synchronizedMap(new HashMap<>());

        return new InventoryCatalog(
            map, "Collections.synchronizedMap"
        );
    }

    // Create inventory backed by ConcurrentHashMap
    public static InventoryCatalog concurrentCatalog() {

        Map<String, Integer> map =
            new ConcurrentHashMap<>();

        return new InventoryCatalog(
            map, "ConcurrentHashMap"
        );
    }

    // Retrieve the current stock of one product
    public int getStock(int productId) {

        validateProductId(productId);

        return inventory.get("Product-" + productId);
    }

    // Atomically increase product stock
    public void addStock(int productId, int amount) {

        validateProductId(productId);

        if (amount <= 0) {
            throw new IllegalArgumentException(
                "Stock increase must be positive."
            );
        }

        String key = "Product-" + productId;

        inventory.compute(key, (name, currentStock) -> {

            if (currentStock == null) {
                throw new IllegalStateException(
                    "Product does not exist."
                );
            }

            return Math.addExact(currentStock, amount);
        });
    }

    private void validateProductId(int productId) {

        if (productId < 0 || productId >= PRODUCT_COUNT) {
            throw new IllegalArgumentException(
                "Invalid product ID: " + productId
            );
        }
    }

    public int getProductCount() {
        return inventory.size();
    }

    public long getTotalStock() {

        long total = 0;

        // Call after worker threads have finished
        for (int stock : inventory.values()) {
            total += stock;
        }

        return total;
    }

    public String getMapType() {
        return mapType;
    }
}
