
public class TradeEventBoundaryTest {

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

    public static void main(String[] args) {

        System.out.println("===== Boundary Validation Tests =====");

        long time = System.currentTimeMillis();

        // Validate common trading event inputs
        expectInvalid("Reject blank sender", () ->
            new FIXOrderEvent("  ", time, 100, "ORD-1")
        );

        expectInvalid("Reject zero timestamp", () ->
            new FIXOrderEvent("FIX", 0, 100, "ORD-2")
        );

        expectInvalid("Reject NaN base price", () ->
            new FIXOrderEvent(
                "FIX", time, Double.NaN, "ORD-3")
        );

        expectInvalid("Reject infinite base price", () ->
            new FIXOrderEvent(
                "FIX", time, Double.POSITIVE_INFINITY, "ORD-4")
        );

        // Validate Reuters quote inputs
        expectInvalid("Reject negative bid", () ->
            new ReutersQuoteEvent(
                "REUTERS", time, 150, -10, 151)
        );

        expectInvalid("Reject zero ask", () ->
            new ReutersQuoteEvent(
                "REUTERS", time, 150, 149, 0)
        );

        expectInvalid("Reject infinite ask", () ->
            new ReutersQuoteEvent(
                "REUTERS", time, 150, 149,
                Double.POSITIVE_INFINITY)
        );

        // Equal bid and ask should produce zero spread
        ReutersQuoteEvent equalQuote =
            new ReutersQuoteEvent(
                "REUTERS", time, 150, 150, 150
            );

        check("Equal prices give zero spread",
            equalQuote.calculateSpread() == 0.0
        );

        System.out.println("\n===== Test Summary =====");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);

        if (failed > 0) {
            throw new AssertionError(
                "Boundary validation tests failed."
            );
        }

        System.out.println("RESULT: ALL TESTS PASSED");
    }
}
