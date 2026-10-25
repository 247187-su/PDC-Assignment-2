
public class WorkerIterationCountTest {

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

    public static void main(String[] args)
            throws InterruptedException {

        System.out.println(
            "===== Worker Iteration Count Tests ====="
        );

        // Create three workers with different workloads
        TelemetryThread worker1 =
                new TelemetryThread("Sensor-A", 1);

        TelemetryThread worker2 =
                new TelemetryThread("Sensor-B", 2);

        TelemetryThread worker3 =
                new TelemetryThread("Sensor-C", 3);

        worker1.setName("Telemetry-1");
        worker2.setName("Telemetry-2");
        worker3.setName("Telemetry-3");

        // Verify initial counter values
        check("All counters initially zero",
            worker1.getCompletedIterations() == 0 &&
            worker2.getCompletedIterations() == 0 &&
            worker3.getCompletedIterations() == 0
        );

        // Start all workers concurrently
        worker1.start();
        worker2.start();
        worker3.start();

        // Wait for their completion
        worker1.join(5000);
        worker2.join(5000);
        worker3.join(5000);

        // Verify termination
        check("Worker 1 terminated", !worker1.isAlive());
        check("Worker 2 terminated", !worker2.isAlive());
        check("Worker 3 terminated", !worker3.isAlive());

        // Verify exact iteration counts
        check("Worker 1 completed 1 iteration",
            worker1.getCompletedIterations() == 1);

        check("Worker 2 completed 2 iterations",
            worker2.getCompletedIterations() == 2);

        check("Worker 3 completed 3 iterations",
            worker3.getCompletedIterations() == 3);

        int total =
            worker1.getCompletedIterations() +
            worker2.getCompletedIterations() +
            worker3.getCompletedIterations();

        check("Total completed iterations equals 6",
            total == 6);

        System.out.println("\n===== Test Summary =====");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
        System.out.println("Total Iterations: " + total);

        if (failed > 0) {
            throw new AssertionError(
                "Worker iteration count tests failed."
            );
        }

        System.out.println("RESULT: ALL TESTS PASSED");
    }
}
