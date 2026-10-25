
public class MatrixBoundaryValidationTest {

    private static int passed = 0;
    private static int failed = 0;

    private static void check(String name, boolean condition) {

        if (condition) {
            System.out.println("[PASS] " + name);
            passed++;
        } else {
            System.out.println("[FAIL] " + name);
            failed++;
        }
    }

    private static void expectInvalid(
            String name, Runnable action) {

        try {
            action.run();
            check(name, false);
        } catch (IllegalArgumentException e) {
            check(name, true);
        }
    }

    private static void testInvalidMatrix(
            String name, double[][] matrix) {

        expectInvalid(name + " - Row-major", () ->
            RowMajorTraversal.sum(matrix)
        );

        expectInvalid(name + " - Column-major", () ->
            ColumnMajorTraversal.sum(matrix)
        );
    }

    public static void main(String[] args) {

        System.out.println(
            "===== Matrix Boundary Validation Tests ====="
        );

        System.out.println(
            "Student: Sultan Majeed | Reg ID: 247187"
        );

        // Test invalid matrix dimensions
        expectInvalid("Reject zero rows", () ->
            MatrixData.createMatrix(0, 5)
        );

        expectInvalid("Reject zero columns", () ->
            MatrixData.createMatrix(5, 0)
        );

        expectInvalid("Reject negative rows", () ->
            MatrixData.createMatrix(-2, 5)
        );

        expectInvalid("Reject negative columns", () ->
            MatrixData.createMatrix(5, -2)
        );

        // Test invalid matrices in both traversals
        testInvalidMatrix(
            "Reject null matrix",
            null
        );

        testInvalidMatrix(
            "Reject empty matrix",
            new double[0][]
        );

        testInvalidMatrix(
            "Reject matrix with no columns",
            new double[][]{{}}
        );

        testInvalidMatrix(
            "Reject null first row",
            new double[][]{null}
        );

        testInvalidMatrix(
            "Reject null later row",
            new double[][]{
                {1.0, 2.0},
                null
            }
        );

        testInvalidMatrix(
            "Reject jagged matrix",
            new double[][]{
                {1.0, 2.0, 3.0},
                {4.0, 5.0}
            }
        );

        // Verify smallest valid matrix
        double[][] single = MatrixData.createMatrix(1, 1);

        check("Single cell row-major calculation",
            RowMajorTraversal.sum(single) == 1.0
        );

        check("Single cell column-major calculation",
            ColumnMajorTraversal.sum(single) == 1.0
        );

        // Verify a valid rectangular matrix
        double[][] rectangular =
                MatrixData.createMatrix(2, 3);

        check("Rectangular row-major calculation",
            RowMajorTraversal.sum(rectangular) == 15.0
        );

        check("Rectangular column-major calculation",
            ColumnMajorTraversal.sum(rectangular) == 15.0
        );

        System.out.println("\n===== Test Summary =====");

        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);

        if (failed > 0) {
            throw new AssertionError(
                "Matrix boundary validation tests failed."
            );
        }

        System.out.println("RESULT: ALL TESTS PASSED");
    }
}
