
public class PacketSymmetryAuditor {

    public static boolean verifyPacket(
            String payload, boolean showSteps) {

        if (payload == null || payload.isEmpty()) {
            throw new IllegalArgumentException(
                "Packet payload cannot be empty."
            );
        }

        int length = payload.length();

        // Create stack and queue
        TransactionStack stack =
                new TransactionStack(length);

        RequestQueue queue =
                new RequestQueue(length);

        // Insert every character into both structures
        for (int i = 0; i < length; i++) {
            char ch = payload.charAt(i);

            stack.push(ch);
            queue.enqueue(ch);
        }

        if (showSteps) {
            System.out.println("\nPacket: " + payload);
            System.out.println("Comparing Stack vs Queue:");
        }

        // Compare reverse and original order
        for (int i = 0; i < length; i++) {

            char stackChar = stack.pop();
            char queueChar = queue.dequeue();

            if (showSteps) {
                System.out.println(
                    "Step " + (i + 1)
                    + ": Stack = " + stackChar
                    + ", Queue = " + queueChar
                );
            }

            if (stackChar != queueChar) {
                return false;
            }
        }

        return true;
    }

    public static void main(String[] args) {

        System.out.println(
            "===== Packet Symmetry Auditor ====="
        );

        System.out.println(
            "Student: Sultan Majeed | Reg ID: 247187"
        );

        // Test a symmetrical packet
        boolean result1 = verifyPacket("RADAR", true);

        System.out.println(
            "Result: " + (
                result1 ? "SYMMETRICAL" : "NOT SYMMETRICAL"
            )
        );

        // Test a non-symmetrical packet
        boolean result2 = verifyPacket("HELLO", true);

        System.out.println(
            "Result: " + (
                result2 ? "SYMMETRICAL" : "NOT SYMMETRICAL"
            )
        );

        System.out.println(
            "\n===== Packet Verification Completed ====="
        );
    }
}
