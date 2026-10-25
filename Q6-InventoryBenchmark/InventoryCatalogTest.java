
public class InventoryCatalogTest {

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

    private static void expectInvalid(
            String name, Runnable action) {

        try {
            action.run();
            check(name, false);
        } catch (IllegalArgumentException e) {
            check(name, true);
        }
    }

    private static void expectOverflow(
            String name, Runnable action) {

        try {
            action.run();
            check(name, false);
        } catch (ArithmeticException e) {
            check(name, true);
        }
    }

    private static void testCatalog(
            String label, InventoryCatalog catalog) {

        System.out.println("\n--- Testing " + label + " ---");

        // Verify initial inventory
        check(label + " - Product count",
            catalog.getProductCount() == 100);

        check(label + " - Initial total stock",
            catalog.getTotalStock() == 100000);

        check(label + " - First product stock",
            catalog.getStock(0) == 1000);

        check(label + " - Last product stock",
            catalog.getStock(99) == 1000);

        // Update two different products
        catalog.addStock(0, 10);
        catalog.addStock(99, 20);

        check(label + " - First product updated",
            catalog.getStock(0) == 1010);

        check(label + " - Last product updated",
            catalog.getStock(99) == 1020);

        check(label + " - Other products unchanged",
            catalog.getStock(1) == 1000);

        check(label + " - Updated total stock",
            catalog.getTotalStock() == 100030);

        // Test invalid product IDs
        expectInvalid(label + " - Reject negative ID", () ->
            catalog.getStock(-1));

        expectInvalid(label + " - Reject invalid ID", () ->
            catalog.getStock(100));

        expectInvalid(label + " - Reject invalid update ID", () ->
            catalog.addStock(100, 1));

        // Test invalid stock additions
        expectInvalid(label + " - Reject zero amount", () ->
            catalog.addStock(0, 0));

        expectInvalid(label + " - Reject negative amount", () ->
            catalog.addStock(0, -5));

        // Test integer overflow protection
        expectOverflow(label + " - Reject stock overflow", () ->
            catalog.addStock(0, Integer.MAX_VALUE));

        // Invalid operations must not change stock
        check(label + " - Stock unchanged after errors",
            catalog.getTotalStock() == 100030);

        System.out.println(
            "--- " + label + " Tests Completed ---"
        );
    }

    public static void main(String[] args) {

        System.out.println(
            "===== Inventory Catalog Correctness Tests ====="
        );

        System.out.println(
            "Student: Sultan Majeed | Reg ID: 247187"
        );

        // Test synchronized HashMap
        testCatalog(
            "SynchronizedMap",
            InventoryCatalog.synchronizedCatalog()
        );

        // Test ConcurrentHashMap
        testCatalog(
            "ConcurrentHashMap",
            InventoryCatalog.concurrentCatalog()
        );

        System.out.println("\n===== Test Summary =====");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);

        if (failed > 0) {
            throw new AssertionError(
                "Inventory correctness tests failed."
            );
        }

        System.out.println("RESULT: ALL TESTS PASSED");
    }
}
