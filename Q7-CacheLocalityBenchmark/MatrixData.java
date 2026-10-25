
public final class MatrixData {

    private MatrixData() {
        // Utility class: no objects required
    }

    public static double[][] createMatrix(
            int rows, int columns) {

        if (rows <= 0 || columns <= 0) {
            throw new IllegalArgumentException(
                "Matrix dimensions must be positive."
            );
        }

        double[][] matrix =
                new double[rows][columns];

        // Generate reproducible values from 1 to 10
        for (int row = 0; row < rows; row++) {

            for (int col = 0; col < columns; col++) {

                matrix[row][col] =
                        (row + col) % 10 + 1;
            }
        }

        return matrix;
    }
}
