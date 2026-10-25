
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ParallelMaxSearch extends SensorSearchEngine {

    public ParallelMaxSearch(
            double[] sensorReadings, int workerCount) {

        super(sensorReadings, workerCount);
    }

    @Override
    public double search() {

        // Create a fixed thread pool
        ExecutorService executor =
                Executors.newFixedThreadPool(workerCount);

        List<Future<Double>> results = new ArrayList<>();

        try {

            // Assign one partition to each worker
            for (int worker = 0; worker < workerCount; worker++) {

                final int start = getStartIndex(worker);
                final int end = getEndIndex(worker);

                Future<Double> result = executor.submit(() -> {

                    double localMax = Double.NEGATIVE_INFINITY;

                    // Find maximum in this partition
                    for (int i = start; i < end; i++) {

                        if (sensorReadings[i] > localMax) {
                            localMax = sensorReadings[i];
                        }
                    }

                    return localMax;
                });

                results.add(result);
            }

            // Combine the maximum from each worker
            double globalMax = Double.NEGATIVE_INFINITY;

            for (Future<Double> result : results) {

                double localMax = result.get();

                if (localMax > globalMax) {
                    globalMax = localMax;
                }
            }

            return globalMax;

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                "Maximum search was interrupted.", e
            );

        } catch (ExecutionException e) {

            throw new IllegalStateException(
                "Maximum search worker failed.", e.getCause()
            );

        } finally {

            executor.shutdownNow();
        }
    }
}
