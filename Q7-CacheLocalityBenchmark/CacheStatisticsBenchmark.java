
import java.util.Arrays;

public class CacheStatisticsBenchmark {

    private static final int SIZE = 2000;
    private static final int WARMUP_ROUNDS = 5;
    private static final int ROUNDS = 12;
    private static final double EXPECTED_SUM = 22000000.0;

    private static volatile double resultSink;

    private static double measure(
            double[][] matrix, boolean rowMajor) {

        long start = System.nanoTime();

        double result = rowMajor
                ? RowMajorTraversal.sum(matrix)
                : ColumnMajorTraversal.sum(matrix);

        long elapsed = System.nanoTime() - start;

        // Prevent incorrect results from being benchmarked
        if (result != EXPECTED_SUM) {
            throw new AssertionError(
                "Matrix sum verification failed."
            );
        }

        resultSink = result;

        return elapsed / 1_000_000.0;
    }

    private static double average(double[] values) {

        double total = 0.0;

        for (double value : values) {
            total += value;
        }

        return total / values.length;
    }

    private static double median(double[] values) {

        double[] sorted = values.clone();
        Arrays.sort(sorted);

        int middle = sorted.length / 2;

        if (sorted.length % 2 == 0) {
            return (sorted[middle - 1]
                    + sorted[middle]) / 2.0;
        }

        return sorted[middle];
    }

    private static void printStatistics(
            String name, double[] times) {

        double[] sorted = times.clone();
        Arrays.sort(sorted);

        double mean = average(times);
        double medianTime = median(times);

        double elementsPerSecond =
                (SIZE * (double) SIZE) * 1000.0 / mean;

        System.out.println("\n--- " + name + " ---");

        System.out.printf(
            "Minimum Time: %.3f ms%n", sorted[0]
        );

        System.out.printf(
            "Maximum Time: %.3f ms%n",
            sorted[sorted.length - 1]
        );

        System.out.printf(
            "Average Time: %.3f ms%n", mean
        );

        System.out.printf(
            "Median Time: %.3f ms%n", medianTime
        );

        System.out.printf(
            "Throughput: %.2f million elements/sec%n",
            elementsPerSecond / 1_000_000.0
        );
    }

    public static void main(String[] args) {

        System.out.println(
            "===== Cache Locality Statistical Benchmark ====="
        );

        System.out.println(
            "Student: Sultan Majeed | Reg ID: 247187"
        );

        double[][] matrix =
                MatrixData.createMatrix(SIZE, SIZE);

        System.out.println("\n===== Configuration =====");

        System.out.println("Matrix Rows: " + SIZE);
        System.out.println("Matrix Columns: " + SIZE);
        System.out.println("Total Elements: " + (SIZE * SIZE));
        System.out.println("Warmup Rounds: " + WARMUP_ROUNDS);
        System.out.println("Measured Rounds: " + ROUNDS);

        // Verify results before measuring performance
        double rowSum = RowMajorTraversal.sum(matrix);
        double columnSum = ColumnMajorTraversal.sum(matrix);

        if (rowSum != EXPECTED_SUM ||
                columnSum != EXPECTED_SUM) {

            throw new AssertionError(
                "Initial matrix verification failed."
            );
        }

        System.out.println("Matrix Verification: PASSED");

        // Warm up both traversal methods
        for (int i = 0; i < WARMUP_ROUNDS; i++) {

            measure(matrix, true);
            measure(matrix, false);
        }

        double[] rowTimes = new double[ROUNDS];
        double[] columnTimes = new double[ROUNDS];

        System.out.println("\n===== Measured Rounds =====");

        for (int i = 0; i < ROUNDS; i++) {

            // Alternate execution order
            if (i % 2 == 0) {

                rowTimes[i] = measure(matrix, true);
                columnTimes[i] = measure(matrix, false);

            } else {

                columnTimes[i] = measure(matrix, false);
                rowTimes[i] = measure(matrix, true);
            }

            System.out.printf(
                "Round %d: Row = %.3f ms"
                + " | Column = %.3f ms%n",
                i + 1,
                rowTimes[i],
                columnTimes[i]
            );
        }

        System.out.println("\n===== Statistical Summary =====");

        printStatistics("Row-Major Traversal", rowTimes);
        printStatistics("Column-Major Traversal", columnTimes);

        double rowAverage = average(rowTimes);
        double columnAverage = average(columnTimes);

        double averageSpeedup =
                columnAverage / rowAverage;

        double medianSpeedup =
                median(columnTimes) / median(rowTimes);

        System.out.println("\n===== Final Comparison =====");

        System.out.printf(
            "Average Speedup: %.2fx%n",
            averageSpeedup
        );

        System.out.printf(
            "Median Speedup: %.2fx%n",
            medianSpeedup
        );

        System.out.println(
            "Faster Average: " +
            (rowAverage < columnAverage
                ? "Row-Major"
                : "Column-Major")
        );

        System.out.println(
            "Row-Major Sum: " + rowSum
        );

        System.out.println(
            "Column-Major Sum: " + columnSum
        );

        System.out.println("Verification: PASSED");

        System.out.println(
            "\n===== Statistical Benchmark Completed ====="
        );

        System.out.println("RESULT: ALL CHECKS PASSED");
    }
}
