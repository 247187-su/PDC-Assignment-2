
import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;

public class PortfolioThresholdTest {

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

    private static boolean closeEnough(
            double actual, double expected) {

        return Math.abs(actual - expected) < 0.0001;
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

        System.out.println(
            "===== Portfolio Threshold Adjustment Tests ====="
        );

        ForkJoinPool pool = new ForkJoinPool();

        try {

            // Test the 5% reduction rule
            double[] values = {
                4000.0, 5000.0, 5000.01, 8000.0, 10000.0
            };

            pool.invoke(
                new PortfolioThresholdTask(
                    values, 0, values.length
                )
            );

            check("Risk below 5000 remains unchanged",
                closeEnough(values[0], 4000.0));

            check("Risk exactly 5000 remains unchanged",
                closeEnough(values[1], 5000.0));

            check("Risk just above 5000 is reduced",
                closeEnough(values[2], 4750.0095));

            check("8000 risk reduces to 7600",
                closeEnough(values[3], 7600.0));

            check("10000 risk reduces to 9500",
                closeEnough(values[4], 9500.0));

            // Test processing only a selected range
            double[] partial = {
                9000.0, 6000.0, 5000.0, 7000.0
            };

            pool.invoke(
                new PortfolioThresholdTask(
                    partial, 1, 3
                )
            );

            check("Selected range is adjusted",
                closeEnough(partial[1], 5700.0));

            check("Threshold value stays unchanged",
                closeEnough(partial[2], 5000.0));

            check("Values outside range stay unchanged",
                closeEnough(partial[0], 9000.0) &&
                closeEnough(partial[3], 7000.0));

            // Test task splitting above threshold
            double[] large = new double[1001];
            Arrays.fill(large, 6000.0);

            pool.invoke(
                new PortfolioThresholdTask(
                    large, 0, large.length
                )
            );

            boolean allAdjusted = true;

            for (double value : large) {
                if (!closeEnough(value, 5700.0)) {
                    allAdjusted = false;
                    break;
                }
            }

            check("All 1001 assets adjusted correctly",
                allAdjusted);

            // Constructor validation
            expectInvalid("Reject null array", () ->
                new PortfolioThresholdTask(null, 0, 1)
            );

            expectInvalid("Reject negative start", () ->
                new PortfolioThresholdTask(values, -1, 3)
            );

            expectInvalid("Reject empty range", () ->
                new PortfolioThresholdTask(values, 2, 2)
            );

            expectInvalid("Reject range beyond array", () ->
                new PortfolioThresholdTask(values, 0, 10)
            );

            System.out.println("\n===== Test Summary =====");
            System.out.println("Passed: " + passed);
            System.out.println("Failed: " + failed);

            if (failed > 0) {
                throw new AssertionError(
                    "Portfolio threshold tests failed."
                );
            }

            System.out.println("RESULT: ALL TESTS PASSED");

        } finally {
            pool.shutdown();
        }
    }
}
