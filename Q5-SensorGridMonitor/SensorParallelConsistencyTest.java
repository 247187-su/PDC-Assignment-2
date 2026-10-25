
import java.util.Arrays;
import java.util.Random;

public class SensorParallelConsistencyTest {

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

    private static double[] createReadings(int size) {

        double[] readings = new double[size];
        Random random = new Random(42);

        for (int i = 0; i < size; i++) {
            readings[i] = 10 + random.nextDouble() * 80;
        }

        // Insert known extreme values
        if (size >= 50000) {
            readings[12345] = -25.50;
            readings[40000] = 145.75;
        } else {
            readings[1] = -25.50;
            readings[size - 2] = 145.75;
        }

        return readings;
    }

    private static double sequentialMin(double[] values) {

        double min = Double.POSITIVE_INFINITY;

        for (double value : values) {
            min = Math.min(min, value);
        }

        return min;
    }

    private static double sequentialMax(double[] values) {

        double max = Double.NEGATIVE_INFINITY;

        for (double value : values) {
            max = Math.max(max, value);
        }

        return max;
    }

    private static void testConsistency(
            String name, double[] readings, int workers) {

        double expectedMin = sequentialMin(readings);
        double expectedMax = sequentialMax(readings);

        boolean consistent = true;

        // Repeat the search three times
        for (int run = 1; run <= 3; run++) {

            ParallelMinSearch minSearch =
                    new ParallelMinSearch(readings, workers);

            ParallelMaxSearch maxSearch =
                    new ParallelMaxSearch(readings, workers);

            double actualMin = minSearch.search();
            double actualMax = maxSearch.search();

            if (actualMin != expectedMin ||
                    actualMax != expectedMax) {
                consistent = false;
                break;
            }
        }

        check(name + " with " + workers + " workers",
                consistent);
    }

    public static void main(String[] args) {

        System.out.println(
            "===== Sensor Parallel Consistency Tests ====="
        );

        // Three different dataset sizes
        double[] small = createReadings(7);
        double[] medium = createReadings(103);
        double[] large = createReadings(50000);

        // Save original data for modification checks
        double[] smallCopy = small.clone();
        double[] largeCopy = large.clone();

        // Small dataset
        testConsistency("Small dataset", small, 1);
        testConsistency("Small dataset", small, 2);
        testConsistency("Small dataset", small, 3);

        // Medium dataset with uneven partitions
        testConsistency("Medium dataset", medium, 1);
        testConsistency("Medium dataset", medium, 2);
        testConsistency("Medium dataset", medium, 4);

        // Large dataset with different worker counts
        testConsistency("Large dataset", large, 1);
        testConsistency("Large dataset", large, 2);
        testConsistency("Large dataset", large, 4);
        testConsistency("Large dataset", large, 8);

        // Search operations must not modify sensor data
        check("Small dataset remains unchanged",
                Arrays.equals(small, smallCopy));

        check("Large dataset remains unchanged",
                Arrays.equals(large, largeCopy));

        System.out.println("\n===== Test Summary =====");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);

        if (failed > 0) {
            throw new AssertionError(
                "Parallel consistency tests failed."
            );
        }

        System.out.println("RESULT: ALL TESTS PASSED");
    }
}
