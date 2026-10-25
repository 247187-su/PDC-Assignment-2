
public class PacketSymmetryTest {

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

    public static void main(String[] args) {

        System.out.println("===== Packet Symmetry Tests =====");

        // Test symmetrical packets
        check("RADAR is symmetrical",
            PacketSymmetryAuditor.verifyPacket("RADAR", false));

        check("LEVEL is symmetrical",
            PacketSymmetryAuditor.verifyPacket("LEVEL", false));

        check("ABBA is symmetrical",
            PacketSymmetryAuditor.verifyPacket("ABBA", false));

        check("Single character is symmetrical",
            PacketSymmetryAuditor.verifyPacket("A", false));

        // Test non-symmetrical packets
        check("HELLO is not symmetrical",
            !PacketSymmetryAuditor.verifyPacket("HELLO", false));

        check("ABC is not symmetrical",
            !PacketSymmetryAuditor.verifyPacket("ABC", false));

        // Test case sensitivity
        check("Character comparison is case-sensitive",
            !PacketSymmetryAuditor.verifyPacket("Radar", false));

        // Test invalid inputs
        expectInvalid("Reject empty packet", () ->
            PacketSymmetryAuditor.verifyPacket("", false)
        );

        expectInvalid("Reject null packet", () ->
            PacketSymmetryAuditor.verifyPacket(null, false)
        );

        System.out.println("\n===== Test Summary =====");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);

        if (failed > 0) {
            throw new AssertionError(
                "Packet symmetry tests failed."
            );
        }

        System.out.println("RESULT: ALL TESTS PASSED");
    }
}
