
import java.util.Random;

public class SensorGridMonitorApp {

    private static double sequentialMin(double[] readings) {

        double min = Double.POSITIVE_INFINITY;

        for (double value : readings) {
            if (value < min) {
                min = value;
            }
        }

        return min;
    }

    private static double sequentialMax(double[] readings) {

        double max = Double.NEGATIVE_INFINITY;

        for (double value : readings) {
            if (value > max) {
                max = value;
            }
        }

        return max;
    }

    public static void main(String[] args) {

        System.out.println(
            "===== Parallel Sensor Grid Monitor ====="
        );

        System.out.println(
            "Student: Sultan Majeed | Reg ID: 247187"
        );

        final int SENSOR_COUNT = 50000;
        final int WORKER_COUNT = 4;

        double[] readings = new double[SENSOR_COUNT];

        Random random = new Random(42);

        // Generate reproducible sensor readings
        for (int i = 0; i < SENSOR_COUNT; i++) {
            readings[i] = 10 + random.nextDouble() * 80;
        }

        // Insert known minimum and maximum readings
        readings[12345] = -25.50;
        readings[40000] = 145.75;

        System.out.println("\n===== Sensor Configuration =====");

        System.out.println("Total Sensor Readings: "
                + SENSOR_COUNT);

        System.out.println("Worker Threads: "
                + WORKER_COUNT);

        System.out.println("Readings Per Worker: "
                + SENSOR_COUNT / WORKER_COUNT);

        // Create both parallel search engines
        SensorSearchEngine minSearch =
                new ParallelMinSearch(
                    readings, WORKER_COUNT
                );

        SensorSearchEngine maxSearch =
                new ParallelMaxSearch(
                    readings, WORKER_COUNT
                );

        System.out.println("\nSearching sensor grid...");

        long startTime = System.nanoTime();

        // Execute parallel minimum and maximum searches
        double globalMin = minSearch.search();
        double globalMax = maxSearch.search();

        long endTime = System.nanoTime();

        double executionTime =
                (endTime - startTime) / 1_000_000.0;

        // Independently verify results
        double expectedMin = sequentialMin(readings);
        double expectedMax = sequentialMax(readings);

        boolean verified =
                globalMin == expectedMin &&
                globalMax == expectedMax &&
                globalMin == -25.50 &&
                globalMax == 145.75;

        System.out.println("\n===== Parallel Search Results =====");

        System.out.printf(
            "Global Minimum Reading: %.2f%n",
            globalMin
        );

        System.out.printf(
            "Global Maximum Reading: %.2f%n",
            globalMax
        );

        System.out.printf(
            "Sequential Minimum: %.2f%n",
            expectedMin
        );

        System.out.printf(
            "Sequential Maximum: %.2f%n",
            expectedMax
        );

        System.out.printf(
            "Execution Time: %.3f ms%n",
            executionTime
        );

        System.out.println(
            "Verification: " +
            (verified ? "PASSED" : "FAILED")
        );

        if (!verified) {
            throw new AssertionError(
                "Parallel sensor search produced incorrect results."
            );
        }

        System.out.println(
            "\n===== Sensor Monitoring Completed ====="
        );
    }
}
