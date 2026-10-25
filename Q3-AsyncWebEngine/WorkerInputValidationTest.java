
public class WorkerInputValidationTest {

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

    public static void main(String[] args) {

        System.out.println(
            "===== Worker Input Validation Tests ====="
        );

        // Log processor validation
        expectInvalid("Reject null log source", () ->
            new LogProcessorTask(null, 1)
        );

        expectInvalid("Reject blank log source", () ->
            new LogProcessorTask("   ", 1)
        );

        expectInvalid("Reject zero log iterations", () ->
            new LogProcessorTask("WebServer", 0)
        );

        expectInvalid("Reject negative log iterations", () ->
            new LogProcessorTask("WebServer", -5)
        );

        // Telemetry worker validation
        expectInvalid("Reject null sensor name", () ->
            new TelemetryThread(null, 1)
        );

        expectInvalid("Reject blank sensor name", () ->
            new TelemetryThread("   ", 1)
        );

        expectInvalid("Reject zero telemetry iterations", () ->
            new TelemetryThread("CPU", 0)
        );

        expectInvalid("Reject negative telemetry iterations", () ->
            new TelemetryThread("CPU", -3)
        );

        // Verify valid objects can be created
        LogProcessorTask logTask =
                new LogProcessorTask("WebServer", 2);

        check("Valid log task created",
                logTask != null);

        TelemetryThread telemetry =
                new TelemetryThread("CPU-Sensor", 3);

        check("Valid telemetry worker created",
                telemetry.getState() == Thread.State.NEW
                && telemetry.getCompletedIterations() == 0);

        System.out.println("\n===== Test Summary =====");

        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);

        if (failed > 0) {
            throw new AssertionError(
                "Worker input validation failed."
            );
        }

        System.out.println("RESULT: ALL TESTS PASSED");
    }
}
