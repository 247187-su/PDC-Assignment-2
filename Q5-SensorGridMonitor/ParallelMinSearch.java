
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ParallelMinSearch extends SensorSearchEngine {

    public ParallelMinSearch(
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

            // Assign a separate partition to each worker
            for (int worker = 0; worker < workerCount; worker++) {

                final int start = getStartIndex(worker);
                final int end = getEndIndex(worker);

                Future<Double> result = executor.submit(() -> {

                    double localMin = Double.POSITIVE_INFINITY;

                    // Find minimum within this partition
                    for (int i = start; i < end; i++) {

                        if (sensorReadings[i] < localMin) {
                            localMin = sensorReadings[i];
                        }
                    }

                    return localMin;
                });

                results.add(result);
            }

            // Combine local minimum values
            double globalMin = Double.POSITIVE_INFINITY;

            for (Future<Double> result : results) {

                double localMin = result.get();

                if (localMin < globalMin) {
                    globalMin = localMin;
                }
            }

            return globalMin;

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                "Minimum search was interrupted.", e
            );

        } catch (ExecutionException e) {

            throw new IllegalStateException(
                "Minimum search worker failed.", e.getCause()
            );

        } finally {

            executor.shutdownNow();
        }
    }
}
