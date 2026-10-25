
public abstract class SensorSearchEngine {

    protected final double[] sensorReadings;
    protected final int workerCount;

    public SensorSearchEngine(
            double[] sensorReadings, int workerCount) {

        if (sensorReadings == null ||
                sensorReadings.length == 0) {
            throw new IllegalArgumentException(
                "Sensor readings cannot be null or empty."
            );
        }

        if (workerCount <= 0 ||
                workerCount > sensorReadings.length) {
            throw new IllegalArgumentException(
                "Invalid number of worker threads."
            );
        }

        // Check that every reading is valid
        for (double reading : sensorReadings) {
            if (!Double.isFinite(reading)) {
                throw new IllegalArgumentException(
                    "Sensor readings must be finite."
                );
            }
        }

        this.sensorReadings = sensorReadings;
        this.workerCount = workerCount;
    }

    // Starting index for each worker
    protected int getStartIndex(int workerId) {
        return (int) (
            (long) workerId *
            sensorReadings.length / workerCount
        );
    }

    // Ending index (exclusive) for each worker
    protected int getEndIndex(int workerId) {
        return (int) (
            (long) (workerId + 1) *
            sensorReadings.length / workerCount
        );
    }

    // Implemented differently by min and max searches
    public abstract double search();
}
