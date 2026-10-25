
import java.util.Arrays;

public class SensorPartitionBoundaryTest {

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

    private static void testPartitions(
            String name, int size, int workers) {

        double[] readings = new double[size];
        Arrays.fill(readings, 40.0);

        ParallelMinSearch minSearch =
                new ParallelMinSearch(readings, workers);

        ParallelMaxSearch maxSearch =
                new ParallelMaxSearch(readings, workers);

        boolean[] covered = new boolean[size];

        boolean contiguous = true;
        boolean noOverlap = true;
        boolean nonEmpty = true;

        int previousEnd = 0;
        int smallestPartition = Integer.MAX_VALUE;
        int largestPartition = 0;

        for (int worker = 0; worker < workers; worker++) {

            int start = minSearch.getStartIndex(worker);
            int end = minSearch.getEndIndex(worker);

            if (start != previousEnd) {
                contiguous = false;
            }

            if (end <= start) {
                nonEmpty = false;
            }

            int partitionSize = end - start;

            smallestPartition =
                    Math.min(smallestPartition, partitionSize);

            largestPartition =
                    Math.max(largestPartition, partitionSize);

            for (int i = start; i < end; i++) {

                if (covered[i]) {
                    noOverlap = false;
                }

                covered[i] = true;
            }

            previousEnd = end;
        }

        for (boolean visited : covered) {
            if (!visited) {
                noOverlap = false;
            }
        }

        check(name + " - Complete coverage",
                contiguous &&
                previousEnd == size &&
                noOverlap);

        check(name + " - No empty partitions",
                nonEmpty);

        check(name + " - Balanced partitions",
                largestPartition - smallestPartition <= 1);

        // Place known extreme values at partition edges
        if (size > 1) {

            int minIndex = minSearch.getEndIndex(0) - 1;
            int maxIndex = maxSearch.getStartIndex(workers - 1);

            readings[minIndex] = -25.50;
            readings[maxIndex] = 145.75;

        }

        double expectedMin = size == 1 ? 40.0 : -25.50;
        double expectedMax = size == 1 ? 40.0 : 145.75;

        check(name + " - Boundary minimum",
                minSearch.search() == expectedMin);

        check(name + " - Boundary maximum",
                maxSearch.search() == expectedMax);
    }

    public static void main(String[] args) {

        System.out.println(
            "===== Sensor Partition Boundary Tests ====="
        );

        testPartitions("Single sensor", 1, 1);
        testPartitions("Uneven partitions", 7, 3);
        testPartitions("One worker per sensor", 8, 8);
        testPartitions("Large sensor grid", 50000, 4);

        // Validate incorrect configurations
        expectInvalid("Reject zero workers", () ->
            new ParallelMinSearch(new double[]{1}, 0)
        );

        expectInvalid("Reject excessive workers", () ->
            new ParallelMaxSearch(new double[]{1}, 2)
        );

        expectInvalid("Reject empty sensor array", () ->
            new ParallelMinSearch(new double[0], 1)
        );

        expectInvalid("Reject NaN sensor reading", () ->
            new ParallelMaxSearch(
                new double[]{1, Double.NaN}, 1)
        );

        System.out.println("\n===== Test Summary =====");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);

        if (failed > 0) {
            throw new AssertionError(
                "Sensor partition boundary tests failed."
            );
        }

        System.out.println("RESULT: ALL TESTS PASSED");
    }
}
