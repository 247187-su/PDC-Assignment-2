
public class PacketDataStructureTest {

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

    private static void expectStateError(
            String name, Runnable action) {
        try {
            action.run();
            check(name, false);
        } catch (IllegalStateException e) {
            check(name, true);
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

    public static void main(String[] args) {

        System.out.println("===== Data Structure Tests =====");

        // Stack tests
        TransactionStack stack = new TransactionStack(2);

        check("Stack initially empty", stack.isEmpty());

        stack.push('A');
        stack.push('B');

        check("Stack full after two pushes", stack.isFull());

        expectStateError("Stack overflow protection",
            () -> stack.push('C'));

        check("Stack follows LIFO",
            stack.pop() == 'B' && stack.pop() == 'A');

        expectStateError("Stack underflow protection",
            () -> stack.pop());

        // Queue tests
        RequestQueue queue = new RequestQueue(3);

        queue.enqueue('A');
        queue.enqueue('B');
        queue.enqueue('C');

        check("Queue full after three inserts", queue.isFull());

        expectStateError("Queue overflow protection",
            () -> queue.enqueue('D'));

        check("Queue follows FIFO",
            queue.dequeue() == 'A' &&
            queue.dequeue() == 'B');

        // Test circular wrap-around
        queue.enqueue('D');
        queue.enqueue('E');

        check("Circular queue wrap-around",
            queue.dequeue() == 'C' &&
            queue.dequeue() == 'D' &&
            queue.dequeue() == 'E');

        check("Queue empty after removals", queue.isEmpty());

        expectStateError("Queue underflow protection",
            () -> queue.dequeue());

        // Invalid capacity tests
        expectInvalid("Reject invalid stack capacity",
            () -> new TransactionStack(0));

        expectInvalid("Reject invalid queue capacity",
            () -> new RequestQueue(0));

        System.out.println("\n===== Test Summary =====");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);

        if (failed > 0) {
            throw new AssertionError(
                "Data structure tests failed.");
        }

        System.out.println("RESULT: ALL TESTS PASSED");
    }
}
