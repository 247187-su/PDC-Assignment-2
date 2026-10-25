
import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;

public class ForkJoinRiskTest {

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

    private static boolean closeEnough(
            double actual, double expected) {

        return Math.abs(actual - expected) < 0.0001;
    }

    public static void main(String[] args) {

        System.out.println(
            "===== ForkJoin Risk Calculation Tests ====="
        );

        ForkJoinPool pool = new ForkJoinPool();

        try {

            // Test 1: Single asset
            double[] single = { 100.50 };

            double result = pool.invoke(
                new ParallelRiskCalculator(
                    single, 0, single.length
                )
            );

            check("Single asset risk",
                closeEnough(result, 100.50));

            // Test 2: Small portfolio
            double[] small = { 100, 200, 300, 400 };

            result = pool.invoke(
                new ParallelRiskCalculator(
                    small, 0, small.length
                )
            );

            check("Small portfolio total",
                closeEnough(result, 1000));

            // Test 3: Threshold boundary
            double[] boundary = new double[1000];
            Arrays.fill(boundary, 2.0);

            result = pool.invoke(
                new ParallelRiskCalculator(
                    boundary, 0, boundary.length
                )
            );

            check("Exactly 1000 assets",
                closeEnough(result, 2000));

            // Test 4: Above threshold
            double[] above = new double[1001];
            Arrays.fill(above, 3.0);

            result = pool.invoke(
                new ParallelRiskCalculator(
                    above, 0, above.length
                )
            );

            check("1001 assets require splitting",
                closeEnough(result, 3003));

            // Test 5: Large portfolio
            double[] large = new double[10000];
            Arrays.fill(large, 5.0);

            result = pool.invoke(
                new ParallelRiskCalculator(
                    large, 0, large.length
                )
            );

            check("Large portfolio total",
                closeEnough(result, 50000));

            // Test 6: Partial array range
            double[] partial = { 10, 20, 30, 40 };

            result = pool.invoke(
                new ParallelRiskCalculator(
                    partial, 1, 3
                )
            );

            check("Partial range calculation",
                closeEnough(result, 50));

            // Test 7: Zero risk values
            double[] zeros = { 0, 0, 0 };

            result = pool.invoke(
                new ParallelRiskCalculator(
                    zeros, 0, zeros.length
                )
            );

            check("Zero risk portfolio",
                closeEnough(result, 0));

            // Constructor validation
            expectInvalid("Reject null array", () ->
                new ParallelRiskCalculator(null, 0, 1)
            );

            expectInvalid("Reject negative start", () ->
                new ParallelRiskCalculator(small, -1, 3)
            );

            expectInvalid("Reject empty range", () ->
                new ParallelRiskCalculator(small, 2, 2)
            );

            expectInvalid("Reject range beyond array", () ->
                new ParallelRiskCalculator(small, 0, 10)
            );

            System.out.println("\n===== Test Summary =====");
            System.out.println("Passed: " + passed);
            System.out.println("Failed: " + failed);

            if (failed > 0) {
                throw new AssertionError(
                    "ForkJoin risk calculation tests failed."
                );
            }

            System.out.println("RESULT: ALL TESTS PASSED");

        } finally {
            pool.shutdown();
        }
    }
}
