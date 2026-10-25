
public class TradeEventTest {

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

    private static void expectInvalid(String name, Runnable action) {
        try {
            action.run();
            check(name, false);
        } catch (IllegalArgumentException e) {
            check(name, true);
        }
    }

    public static void main(String[] args) {

        System.out.println("===== Trading Engine Tests =====");

        long time = System.currentTimeMillis();

        FIXOrderEvent fix = new FIXOrderEvent(
            "FIX-TEST", time, 1000.00, "ORD-001"
        );

        ReutersQuoteEvent quote = new ReutersQuoteEvent(
            "REUTERS-TEST", time, 150.00, 149.50, 150.25
        );

        // Test 1: FIX transaction fee
        check("FIX fee calculation",
            Math.abs(fix.calculateFee() - 1.50) < 0.0001);

        // Test 2: FIX total cost
        check("FIX total cost",
            Math.abs(fix.calculateTotalCost() - 1001.50) < 0.0001);

        // Test 3: Reuters spread
        check("Reuters bid-ask spread",
            Math.abs(quote.calculateSpread() - 0.75) < 0.0001);

        // Test 4: Reject empty order ID
        expectInvalid("Reject empty order ID", () ->
            new FIXOrderEvent("FIX", time, 1000, "")
        );

        // Test 5: Reject invalid bid-ask prices
        expectInvalid("Reject ask below bid", () ->
            new ReutersQuoteEvent(
                "REUTERS", time, 150, 151, 149
            )
        );

        // Test 6: Reject negative base price
        expectInvalid("Reject negative price", () ->
            new FIXOrderEvent("FIX", time, -100, "ORD-002")
        );

        System.out.println("\n===== Test Summary =====");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);

        if (failed > 0) {
            throw new AssertionError("Trading engine tests failed.");
        }

        System.out.println("RESULT: ALL TESTS PASSED");
    }
}
