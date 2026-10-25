
public class AsyncWebEngineApp {

    public static void main(String[] args)
            throws InterruptedException {

        System.out.println(
            "===== Asynchronous Web Engine ====="
        );

        System.out.println(
            "Student: Sultan Majeed | Reg ID: 247187"
        );

        // Worker 1: Runnable implementation
        LogProcessorTask logTask =
                new LogProcessorTask("WebServer", 5);

        Thread logThread =
                new Thread(logTask, "Log-Worker");

        // Worker 2: Thread subclass
        TelemetryThread telemetryThread =
                new TelemetryThread("CPU-Sensor", 5);

        System.out.println("\nStarting both workers...");

        long startTime = System.nanoTime();

        // Start both threads concurrently
        logThread.start();
        telemetryThread.start();

        // Wait for both workers to finish
        logThread.join();
        telemetryThread.join();

        long endTime = System.nanoTime();

        double elapsedSeconds =
                (endTime - startTime) / 1_000_000_000.0;

        System.out.println("\n===== Execution Summary =====");

        System.out.println(
            "Log Worker Status: " +
            logThread.getState()
        );

        System.out.println(
            "Telemetry Worker Status: " +
            telemetryThread.getState()
        );

        System.out.println(
            "Telemetry Samples Completed: " +
            telemetryThread.getCompletedIterations()
        );

        System.out.printf(
            "Total Execution Time: %.2f seconds%n",
            elapsedSeconds
        );

        System.out.println(
            "===== Program Completed ====="
        );
    }
}
