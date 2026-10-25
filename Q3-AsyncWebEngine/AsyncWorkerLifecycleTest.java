
public class AsyncWorkerLifecycleTest {

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

        System.out.println("===== Worker Lifecycle Tests =====");

        // Create a Runnable-based worker
        LogProcessorTask logTask =
                new LogProcessorTask("TestServer", 1);

        Thread logThread =
                new Thread(logTask, "Test-Log-Worker");

        // Create a Thread-based worker
        TelemetryThread telemetryThread =
                new TelemetryThread("TestSensor", 1);

        // Check initial states
        check("Log thread initially NEW",
                logThread.getState() == Thread.State.NEW);

        check("Telemetry thread initially NEW",
                telemetryThread.getState() == Thread.State.NEW);

        // Start both workers
        logThread.start();
        telemetryThread.start();

        // Wait for both to complete
        logThread.join();
        telemetryThread.join();

        // Check final states
        check("Log thread TERMINATED",
                logThread.getState() == Thread.State.TERMINATED);

        check("Telemetry thread TERMINATED",
                telemetryThread.getState() == Thread.State.TERMINATED);

        // Verify completed work
        check("Telemetry completed one sample",
                telemetryThread.getCompletedIterations() == 1);

        System.out.println("\n===== Test Summary =====");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);

        if (failed > 0) {
            throw new AssertionError(
                "Worker lifecycle tests failed."
            );
        }

        System.out.println("RESULT: ALL TESTS PASSED");
    }
}
