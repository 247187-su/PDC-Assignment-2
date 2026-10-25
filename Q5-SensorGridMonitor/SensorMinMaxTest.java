
public class SensorMinMaxTest {

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

    private static void testDataset(
            String name,
            double[] readings,
            int workers,
            double expectedMin,
            double expectedMax) {

        // Execute parallel minimum search
        ParallelMinSearch minSearch =
                new ParallelMinSearch(readings, workers);

        // Execute parallel maximum search
        ParallelMaxSearch maxSearch =
                new ParallelMaxSearch(readings, workers);

        double actualMin = minSearch.search();
        double actualMax = maxSearch.search();

        check(name + " - Minimum",
                actualMin == expectedMin);

        check(name + " - Maximum",
                actualMax == expectedMax);
    }

    public static void main(String[] args) {

        System.out.println(
            "===== Sensor Minimum and Maximum Tests ====="
        );

        // Test 1: Mixed positive and negative readings
        testDataset(
            "Mixed readings",
            new double[]{12.5, -25.5, 42.0, 145.75},
            2,
            -25.5,
            145.75
        );

        // Test 2: All negative readings
        testDataset(
            "Negative readings",
            new double[]{-10, -40, -7, -3},
            2,
            -40,
            -3
        );

        // Test 3: Identical readings
        testDataset(
            "Identical readings",
            new double[]{5.5, 5.5, 5.5, 5.5},
            4,
            5.5,
            5.5
        );

        // Test 4: Only one sensor
        testDataset(
            "Single reading",
            new double[]{77.0},
            1,
            77.0,
            77.0
        );

        // Test 5: Uneven partition sizes
        testDataset(
            "Uneven partitions",
            new double[]{9, 3, 7, 15, 0, -2, 28},
            3,
            -2,
            28
        );

        // Test 6: One worker processes all values
        testDataset(
            "Single worker",
            new double[]{6, 1, 11, 5},
            1,
            1,
            11
        );

        System.out.println("\n===== Test Summary =====");

        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);

        if (failed > 0) {
            throw new AssertionError(
                "Sensor minimum/maximum tests failed."
            );
        }

        System.out.println("RESULT: ALL TESTS PASSED");
    }
}
