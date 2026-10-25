
import java.util.concurrent.RecursiveAction;

public class PortfolioThresholdTask extends RecursiveAction {

    private static final int THRESHOLD = 1000;
    private static final double RISK_LIMIT = 5000.0;
    private static final double REDUCTION_RATE = 0.05;

    private final double[] riskValues;
    private final int start;
    private final int end;

    public PortfolioThresholdTask(
            double[] riskValues, int start, int end) {

        if (riskValues == null) {
            throw new IllegalArgumentException(
                "Risk values cannot be null."
            );
        }

        if (start < 0 || end > riskValues.length
                || start >= end) {
            throw new IllegalArgumentException(
                "Invalid portfolio range."
            );
        }

        this.riskValues = riskValues;
        this.start = start;
        this.end = end;
    }

    @Override
    protected void compute() {

        int length = end - start;

        // Process small portions directly
        if (length <= THRESHOLD) {

            for (int i = start; i < end; i++) {

                if (riskValues[i] > RISK_LIMIT) {

                    riskValues[i] =
                        riskValues[i] * (1.0 - REDUCTION_RATE);
                }
            }

            return;
        }

        // Divide large portions into smaller tasks
        int middle = start + length / 2;

        PortfolioThresholdTask leftTask =
                new PortfolioThresholdTask(
                    riskValues, start, middle
                );

        PortfolioThresholdTask rightTask =
                new PortfolioThresholdTask(
                    riskValues, middle, end
                );

        // Execute both tasks using Fork/Join
        invokeAll(leftTask, rightTask);
    }
}
