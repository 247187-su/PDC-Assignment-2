
public final class ColumnMajorTraversal {

    private ColumnMajorTraversal() {
        // Utility class: no instances required
    }

    public static double sum(double[][] matrix) {

        if (matrix == null || matrix.length == 0) {
            throw new IllegalArgumentException(
                "Matrix cannot be null or empty."
            );
        }

        if (matrix[0] == null || matrix[0].length == 0) {
            throw new IllegalArgumentException(
                "Matrix must contain columns."
            );
        }

        int rows = matrix.length;
        int columns = matrix[0].length;

        // Validate that every row has equal length
        for (double[] row : matrix) {

            if (row == null || row.length != columns) {
                throw new IllegalArgumentException(
                    "Matrix must be rectangular."
                );
            }
        }

        double total = 0.0;

        // Column-major order: visit each column first
        for (int col = 0; col < columns; col++) {

            for (int row = 0; row < rows; row++) {

                total += matrix[row][col];
            }
        }

        return total;
    }
}
