
import java.util.concurrent.RecursiveTask;

public class ParallelRiskCalculator
        extends RecursiveTask<Double> {

    private static final int THRESHOLD = 1000;

    private final double[] riskValues;
    private final int start;
    private final int end;

    public ParallelRiskCalculator(
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
    protected Double compute() {

        int length = end - start;

        // Small tasks are calculated directly
        if (length <= THRESHOLD) {

            double sum = 0.0;

            for (int i = start; i < end; i++) {
                sum += riskValues[i];
            }

            return sum;
        }

        // Divide a large task into two smaller tasks
        int middle = start + length / 2;

        ParallelRiskCalculator leftTask =
                new ParallelRiskCalculator(
                    riskValues, start, middle
                );

        ParallelRiskCalculator rightTask =
                new ParallelRiskCalculator(
                    riskValues, middle, end
                );

        // Execute left task asynchronously
        leftTask.fork();

        // Calculate right task in current worker
        double rightResult = rightTask.compute();

        // Wait for left task and combine results
        double leftResult = leftTask.join();

        return leftResult + rightResult;
    }
}
