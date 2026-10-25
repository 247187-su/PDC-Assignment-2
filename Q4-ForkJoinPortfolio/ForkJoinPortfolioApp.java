
import java.util.Random;
import java.util.concurrent.ForkJoinPool;

public class ForkJoinPortfolioApp {

    public static void main(String[] args) {

        System.out.println(
            "===== Fork/Join Portfolio Risk Calculator ====="
        );
        System.out.println(
            "Student: Sultan Majeed | Reg ID: 247187"
        );

        final int ASSET_COUNT = 10000;
        double[] riskValues = new double[ASSET_COUNT];

        Random random = new Random(42);

        // Generate portfolio risk values
        for (int i = 0; i < ASSET_COUNT; i++) {
            riskValues[i] = 1000 + random.nextDouble() * 9000;
        }

        ForkJoinPool pool = new ForkJoinPool();

        try {
            System.out.println("\n===== Portfolio Details =====");
            System.out.println("Total Assets: " + ASSET_COUNT);
            System.out.println(
                "Available Processors: " +
                Runtime.getRuntime().availableProcessors()
            );
            System.out.println(
                "ForkJoin Parallelism: " +
                pool.getParallelism()
            );

            // Calculate original portfolio risk
            ParallelRiskCalculator originalTask =
                new ParallelRiskCalculator(
                    riskValues, 0, riskValues.length
                );

            double originalRisk = pool.invoke(originalTask);

            System.out.printf(
                "\nOriginal Portfolio Risk: $%.2f%n",
                originalRisk
            );

            // Apply parallel risk reduction
            PortfolioThresholdTask adjustmentTask =
                new PortfolioThresholdTask(
                    riskValues, 0, riskValues.length
                );

            pool.invoke(adjustmentTask);

            // Calculate adjusted portfolio risk
            ParallelRiskCalculator adjustedTask =
                new ParallelRiskCalculator(
                    riskValues, 0, riskValues.length
                );

            double adjustedRisk = pool.invoke(adjustedTask);

            double totalReduction = originalRisk - adjustedRisk;

            System.out.println("\n===== Risk Adjustment Results =====");

            System.out.printf(
                "Adjusted Portfolio Risk: $%.2f%n",
                adjustedRisk
            );

            System.out.printf(
                "Total Risk Reduction: $%.2f%n",
                totalReduction
            );

            System.out.println(
                "Reduction Rule: 5% for assets above $5000"
            );

            boolean verified =
                adjustedRisk <= originalRisk &&
                totalReduction >= 0;

            System.out.println(
                "Verification: " +
                (verified ? "PASSED" : "FAILED")
            );

            if (!verified) {
                throw new AssertionError(
                    "Portfolio risk verification failed."
                );
            }

            System.out.println(
                "\n===== Portfolio Processing Completed ====="
            );

        } finally {
            pool.shutdown();
        }
    }
}
