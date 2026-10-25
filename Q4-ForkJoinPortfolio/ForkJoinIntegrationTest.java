
import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;

public class ForkJoinIntegrationTest {

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

        double tolerance = Math.max(
            0.000001, Math.abs(expected) * 1e-10
        );

        return Math.abs(actual - expected) <= tolerance;
    }

    private static double sequentialSum(double[] values) {

        double total = 0.0;

        for (double value : values) {
            total += value;
        }

        return total;
    }

    private static void testPortfolio(
            ForkJoinPool pool, int size) {

        double[] parallel = new double[size];

        // Create reproducible portfolio values
        for (int i = 0; i < size; i++) {
            parallel[i] = 2000.0 + (i % 9) * 750.0;
        }

        double[] sequential = parallel.clone();

        // Compare original risk calculations
        double originalSequential = sequentialSum(sequential);

        double originalParallel = pool.invoke(
            new ParallelRiskCalculator(
                parallel, 0, parallel.length
            )
        );

        check("Original risk matches for size " + size,
            closeEnough(originalParallel, originalSequential)
        );

        // Apply adjustment sequentially
        for (int i = 0; i < sequential.length; i++) {

            if (sequential[i] > 5000.0) {
                sequential[i] *= 0.95;
            }
        }

        // Apply adjustment using Fork/Join
        pool.invoke(
            new PortfolioThresholdTask(
                parallel, 0, parallel.length
            )
        );

        check("Adjusted assets match for size " + size,
            Arrays.equals(parallel, sequential)
        );

        // Compare adjusted risk calculations
        double adjustedSequential = sequentialSum(sequential);

        double adjustedParallel = pool.invoke(
            new ParallelRiskCalculator(
                parallel, 0, parallel.length
            )
        );

        check("Adjusted risk matches for size " + size,
            closeEnough(adjustedParallel, adjustedSequential)
        );

        check("Risk does not increase for size " + size,
            adjustedParallel <= originalParallel
        );
    }

    public static void main(String[] args) {

        System.out.println(
            "===== Fork/Join Integration Tests ====="
        );

        ForkJoinPool pool = new ForkJoinPool();

        try {

            // Small portfolio and splitting boundaries
            testPortfolio(pool, 1);
            testPortfolio(pool, 1000);
            testPortfolio(pool, 1001);
            testPortfolio(pool, 10000);

            // Test adjustment of a selected array range
            double[] partial = {
                9000.0, 7000.0, 4000.0, 6000.0
            };

            pool.invoke(
                new PortfolioThresholdTask(partial, 1, 3)
            );

            check("Partial range adjustment correct",
                closeEnough(partial[0], 9000.0) &&
                closeEnough(partial[1], 6650.0) &&
                closeEnough(partial[2], 4000.0) &&
                closeEnough(partial[3], 6000.0)
            );

            double partialRisk = pool.invoke(
                new ParallelRiskCalculator(partial, 1, 3)
            );

            check("Partial range risk calculation correct",
                closeEnough(partialRisk, 10650.0)
            );

            System.out.println("\n===== Test Summary =====");
            System.out.println("Passed: " + passed);
            System.out.println("Failed: " + failed);

            if (failed > 0) {
                throw new AssertionError(
                    "Fork/Join integration tests failed."
                );
            }

            System.out.println("RESULT: ALL TESTS PASSED");

        } finally {
            pool.shutdown();
        }
    }
}
