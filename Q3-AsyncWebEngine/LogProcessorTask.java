
public class LogProcessorTask implements Runnable {

    private final String sourceName;
    private final int iterations;

    public LogProcessorTask(String sourceName, int iterations) {

        if (sourceName == null || sourceName.isBlank()) {
            throw new IllegalArgumentException(
                "Source name cannot be empty."
            );
        }

        if (iterations <= 0) {
            throw new IllegalArgumentException(
                "Iterations must be positive."
            );
        }

        this.sourceName = sourceName;
        this.iterations = iterations;
    }

    @Override
    public void run() {

        String worker = Thread.currentThread().getName();

        System.out.println(
            "[" + worker + "] Log processing started."
        );

        for (int i = 1; i <= iterations; i++) {

            if (Thread.currentThread().isInterrupted()) {
                System.out.println(
                    "[" + worker + "] Processing interrupted."
                );
                return;
            }

            System.out.println(
                "[" + worker + "] Processing "
                + sourceName + " - Log " + i
            );

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();

                System.out.println(
                    "[" + worker + "] Processing interrupted."
                );
                return;
            }
        }

        System.out.println(
            "[" + worker + "] Log processing completed."
        );
    }
}
