
public class CacheLocalityBenchmark {

    private static final int ROWS = 2000;
    private static final int COLUMNS = 2000;
    private static final int WARMUP_ROUNDS = 3;
    private static final int BENCHMARK_ROUNDS = 10;

    private static final double EXPECTED_SUM = 22000000.0;

    private static long measureRowMajor(double[][] matrix) {

        long start = System.nanoTime();

        double result = RowMajorTraversal.sum(matrix);

        long elapsed = System.nanoTime() - start;

        if (result != EXPECTED_SUM) {
            throw new AssertionError(
                "Row-major calculation is incorrect."
            );
        }

        return elapsed;
    }

    private static long measureColumnMajor(double[][] matrix) {

        long start = System.nanoTime();

        double result = ColumnMajorTraversal.sum(matrix);

        long elapsed = System.nanoTime() - start;

        if (result != EXPECTED_SUM) {
            throw new AssertionError(
                "Column-major calculation is incorrect."
            );
        }

        return elapsed;
    }

    public static void main(String[] args) {

        System.out.println(
            "===== Cache Locality Performance Benchmark ====="
        );

        System.out.println(
            "Student: Sultan Majeed | Reg ID: 247187"
        );

        // Generate the matrix
        double[][] matrix =
                MatrixData.createMatrix(ROWS, COLUMNS);

        System.out.println("\n===== Matrix Configuration =====");

        System.out.println("Rows: " + ROWS);
        System.out.println("Columns: " + COLUMNS);
        System.out.println(
            "Total Elements: " + (ROWS * COLUMNS)
        );
        System.out.println(
            "Benchmark Rounds: " + BENCHMARK_ROUNDS
        );

        // Verify results before benchmarking
        double rowSum = RowMajorTraversal.sum(matrix);
        double columnSum = ColumnMajorTraversal.sum(matrix);

        boolean correct =
                rowSum == columnSum &&
                rowSum == EXPECTED_SUM;

        System.out.println("\n===== Correctness Check =====");

        System.out.printf(
            "Row-Major Sum: %.2f%n", rowSum
        );

        System.out.printf(
            "Column-Major Sum: %.2f%n", columnSum
        );

        System.out.println(
            "Results Match: " + (correct ? "YES" : "NO")
        );

        if (!correct) {
            throw new AssertionError(
                "Matrix traversal results do not match."
            );
        }

        // Warm up the Java Virtual Machine
        for (int i = 0; i < WARMUP_ROUNDS; i++) {
            measureRowMajor(matrix);
            measureColumnMajor(matrix);
        }

        long rowTotal = 0;
        long columnTotal = 0;

        System.out.println("\n===== Benchmark Rounds =====");

        for (int round = 1; round <= BENCHMARK_ROUNDS; round++) {

            long rowTime;
            long columnTime;

            // Alternate order to reduce ordering bias
            if (round % 2 == 1) {

                rowTime = measureRowMajor(matrix);
                columnTime = measureColumnMajor(matrix);

            } else {

                columnTime = measureColumnMajor(matrix);
                rowTime = measureRowMajor(matrix);
            }

            rowTotal += rowTime;
            columnTotal += columnTime;

            System.out.printf(
                "Round %d: Row = %.3f ms"
                + " | Column = %.3f ms%n",
                round,
                rowTime / 1_000_000.0,
                columnTime / 1_000_000.0
            );
        }

        double rowAverage =
                rowTotal / (BENCHMARK_ROUNDS * 1_000_000.0);

        double columnAverage =
                columnTotal / (BENCHMARK_ROUNDS * 1_000_000.0);

        double speedup = columnAverage / rowAverage;

        System.out.println("\n===== Final Benchmark Results =====");

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

        System.out.println(
            "\n===== Cache Locality Benchmark Completed ====="
        );
    }
}
