
public class MultiSizeCacheBenchmark {

    private static final int[] MATRIX_SIZES = {
        100, 500, 1000, 2000
    };

    private static final int WARMUP_ROUNDS = 3;
    private static final int BENCHMARK_ROUNDS = 5;

    private static volatile double resultSink;

    private static long measureTraversal(
            double[][] matrix,
            boolean rowMajor,
            double expectedSum) {

        long startTime = System.nanoTime();

        double result;

        if (rowMajor) {
            result = RowMajorTraversal.sum(matrix);
        } else {
            result = ColumnMajorTraversal.sum(matrix);
        }

        long elapsed = System.nanoTime() - startTime;

        if (result != expectedSum) {
            throw new AssertionError(
                "Matrix traversal produced an incorrect sum."
            );
        }

        resultSink = result;

        return elapsed;
    }

    private static void benchmarkMatrix(int size) {

        System.out.println(
            "\n===== Matrix Size: " + size +
            " x " + size + " ====="
        );

        double[][] matrix =
                MatrixData.createMatrix(size, size);

        int totalElements = size * size;

        // Sizes are multiples of 10.
        // Each complete value cycle averages 5.5.
        double expectedSum = totalElements * 5.5;

        // Verify both methods before measuring
        double rowSum = RowMajorTraversal.sum(matrix);
        double columnSum = ColumnMajorTraversal.sum(matrix);

        if (rowSum != expectedSum ||
            columnSum != expectedSum) {

            throw new AssertionError(
                "Initial matrix verification failed."
            );
        }

        // Warm up both traversal methods
        for (int i = 0; i < WARMUP_ROUNDS; i++) {

            measureTraversal(matrix, true, expectedSum);
            measureTraversal(matrix, false, expectedSum);
        }

        long rowTotal = 0;
        long columnTotal = 0;

        System.out.println(
            "Total Elements: " + totalElements
        );

        System.out.println(
            "Expected Sum: " + expectedSum
        );

        System.out.println("\n--- Benchmark Rounds ---");

        for (int round = 1;
                round <= BENCHMARK_ROUNDS;
                round++) {

            long rowTime;
            long columnTime;

            // Alternate execution order for fairness
            if (round % 2 == 1) {

                rowTime = measureTraversal(
                    matrix, true, expectedSum
                );

                columnTime = measureTraversal(
                    matrix, false, expectedSum
                );

            } else {

                columnTime = measureTraversal(
                    matrix, false, expectedSum
                );

                rowTime = measureTraversal(
                    matrix, true, expectedSum
                );
            }

            rowTotal += rowTime;
            columnTotal += columnTime;

            System.out.printf(
                "Round %d: Row = %.3f ms | "
                + "Column = %.3f ms%n",
                round,
                rowTime / 1_000_000.0,
                columnTime / 1_000_000.0
            );
        }

        double rowAverage =
                rowTotal /
                (BENCHMARK_ROUNDS * 1_000_000.0);

        double columnAverage =
                columnTotal /
                (BENCHMARK_ROUNDS * 1_000_000.0);

        double speedup =
                columnAverage / rowAverage;

        System.out.println("\n--- Final Results ---");

        System.out.printf(
            "Average Row-Major Time: %.3f ms%n",
            rowAverage
        );

        System.out.printf(
            "Average Column-Major Time: %.3f ms%n",
            columnAverage
        );

        System.out.printf(
            "Row-Major Speedup: %.2fx%n",
            speedup
        );

        System.out.println(
            "Faster Traversal: " +
            (rowAverage < columnAverage
                ? "Row-Major"
                : "Column-Major")
        );

        System.out.println("Verification: PASSED");
    }

    public static void main(String[] args) {

        System.out.println(
            "===== Multi-Size Cache Locality Benchmark ====="
        );

        System.out.println(
            "Student: Sultan Majeed | Reg ID: 247187"
        );

        System.out.println(
            "Benchmark Rounds: " + BENCHMARK_ROUNDS
        );

        System.out.println(
            "Warmup Rounds: " + WARMUP_ROUNDS
        );

        for (int size : MATRIX_SIZES) {
            benchmarkMatrix(size);
        }

        System.out.println(
            "\n===== All Matrix Benchmarks Completed ====="
        );

        System.out.println(
            "RESULT: ALL CHECKS PASSED"
        );
    }
}
