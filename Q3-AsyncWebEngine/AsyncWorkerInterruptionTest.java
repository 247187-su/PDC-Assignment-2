
public class AsyncWorkerInterruptionTest {

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
            "===== Worker Interruption Tests ====="
        );

        // Create workers with five iterations each
        LogProcessorTask logTask =
                new LogProcessorTask("TestServer", 5);

        Thread logThread =
                new Thread(logTask, "Interrupt-Log-Worker");

        TelemetryThread telemetryThread =
                new TelemetryThread("TestSensor", 5);

        // Start both workers
        logThread.start();
        telemetryThread.start();

        // Give the workers a moment to start
        Thread.sleep(150);

        System.out.println("\nSending interruption signals...");

        // Interrupt both workers
        logThread.interrupt();
        telemetryThread.interrupt();

        // Wait for termination
        logThread.join(2000);
        telemetryThread.join(2000);

        // Verify both workers stopped
        check("Log worker terminated",
                !logThread.isAlive());

        check("Telemetry worker terminated",
                !telemetryThread.isAlive());

        // Verify telemetry stopped before five samples
        check("Telemetry stopped early",
                telemetryThread.getCompletedIterations() < 5);

        // Verify interrupt status is preserved
        check("Log interrupt status preserved",
                logThread.isInterrupted());

        check("Telemetry interrupt status preserved",
                telemetryThread.isInterrupted());

        System.out.println("\n===== Test Summary =====");

        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);

        if (failed > 0) {
            throw new AssertionError(
                "Worker interruption tests failed."
            );
        }

        System.out.println("RESULT: ALL TESTS PASSED");
    }
}
