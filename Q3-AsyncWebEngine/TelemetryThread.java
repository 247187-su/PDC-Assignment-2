
public class TelemetryThread extends Thread {

    private final String sensorName;
    private final int iterations;
    private volatile int completedIterations;

    public TelemetryThread(String sensorName, int iterations) {

        if (sensorName == null || sensorName.isBlank()) {
            throw new IllegalArgumentException(
                "Sensor name cannot be empty."
            );
        }

        if (iterations <= 0) {
            throw new IllegalArgumentException(
                "Iterations must be positive."
            );
        }

        this.sensorName = sensorName;
        this.iterations = iterations;
        this.completedIterations = 0;

        setName("Telemetry-Worker");
    }

    @Override
    public void run() {

        System.out.println(
            "[" + getName() + "] Telemetry monitoring started."
        );

        for (int i = 1; i <= iterations; i++) {

            if (isInterrupted()) {
                System.out.println(
                    "[" + getName() + "] Monitoring interrupted."
                );
                return;
            }

            System.out.println(
                "[" + getName() + "] Reading "
                + sensorName + " - Sample " + i
            );

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {

                interrupt();

                System.out.println(
                    "[" + getName() + "] Monitoring interrupted."
                );
                return;
            }

            completedIterations++;
        }

        System.out.println(
            "[" + getName() + "] Telemetry monitoring completed."
        );
    }

    public int getCompletedIterations() {
        return completedIterations;
    }
}
