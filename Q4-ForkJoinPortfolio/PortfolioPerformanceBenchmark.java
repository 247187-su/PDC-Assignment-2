
import java.util.Random;
import java.util.concurrent.ForkJoinPool;

public class PortfolioPerformanceBenchmark {

    private static double sequentialSum(double[] values) {

        double total = 0.0;

        for (double value : values) {
            total += value;
        }

        return total;
    }

    public static void main(String[] args) {

        System.out.println(
            "===== Portfolio Performance Benchmark ====="
        );

        System.out.println(
            "Student: Sultan Majeed | Reg ID: 247187"
        );

        final int SIZE = 10000;
        final int ROUNDS = 20;

        double[] riskValues = new double[SIZE];
        Random random = new Random(42);

        // Generate identical data for both approaches
        for (int i = 0; i < SIZE; i++) {
            riskValues[i] =
                1000 + random.nextDouble() * 9000;
        }

        ForkJoinPool pool = new ForkJoinPool();

        try {

            // Warm up both approaches
            for (int i = 0; i < 5; i++) {

                sequentialSum(riskValues);

                pool.invoke(
                    new ParallelRiskCalculator(
                        riskValues, 0, SIZE
                    )
                );
            }

            long sequentialTotal = 0;
            long parallelTotal = 0;

            double sequentialResult = 0;
            double parallelResult = 0;

            // Alternate execution order between rounds
            for (int round = 0; round < ROUNDS; round++) {

                if (round % 2 == 0) {

                    long start = System.nanoTime();

                    sequentialResult =
                        sequentialSum(riskValues);

                    sequentialTotal +=
                        System.nanoTime() - start;

                    start = System.nanoTime();

                    parallelResult = pool.invoke(
                        new ParallelRiskCalculator(
                            riskValues, 0, SIZE
                        )
                    );

                    parallelTotal +=
                        System.nanoTime() - start;

                } else {

                    long start = System.nanoTime();

                    parallelResult = pool.invoke(
                        new ParallelRiskCalculator(
                            riskValues, 0, SIZE
                        )
                    );

                    parallelTotal +=
                        System.nanoTime() - start;

                    start = System.nanoTime();

                    sequentialResult =
                        sequentialSum(riskValues);

                    sequentialTotal +=
                        System.nanoTime() - start;
                }
            }

            double sequentialMs =
                sequentialTotal / (ROUNDS * 1_000_000.0);

            double parallelMs =
                parallelTotal / (ROUNDS * 1_000_000.0);

            double speedup = sequentialMs / parallelMs;

            double tolerance =
                Math.max(0.000001,
                         Math.abs(sequentialResult) * 1e-10);

            boolean matched =
                Math.abs(sequentialResult - parallelResult)
                <= tolerance;

            System.out.println("\n===== Benchmark Results =====");

            System.out.println("Portfolio Assets: " + SIZE);
            System.out.println("Benchmark Rounds: " + ROUNDS);

            System.out.printf(
                "Sequential Risk: $%.2f%n",
                sequentialResult
            );

            System.out.printf(
                "Parallel Risk: $%.2f%n",
                parallelResult
            );

            System.out.printf(
                "Average Sequential Time: %.4f ms%n",
                sequentialMs
            );

            System.out.printf(
                "Average Parallel Time: %.4f ms%n",
                parallelMs
            );

            System.out.printf(
                "Speedup: %.2fx%n",
                speedup
            );

            System.out.println(
                "Results Match: " +
                (matched ? "YES" : "NO")
            );

            System.out.println(
                "Verification: " +
                (matched ? "PASSED" : "FAILED")
            );

            if (!matched) {
                throw new AssertionError(
                    "Sequential and parallel results differ."
                );
            }

            System.out.println(
                "\n===== Benchmark Completed ====="
            );

        } finally {
            pool.shutdown();
        }
    }
}
