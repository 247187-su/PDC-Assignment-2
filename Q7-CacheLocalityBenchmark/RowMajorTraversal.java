
public final class RowMajorTraversal {

    private RowMajorTraversal() {
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

        // Validate the matrix before traversal
        for (double[] row : matrix) {

            if (row == null || row.length != columns) {
                throw new IllegalArgumentException(
                    "Matrix must be rectangular."
                );
            }
        }

        double total = 0.0;

        // Row-major order: process each row completely
        for (int row = 0; row < rows; row++) {

            for (int col = 0; col < columns; col++) {

                total += matrix[row][col];
            }
        }

        return total;
    }
}
