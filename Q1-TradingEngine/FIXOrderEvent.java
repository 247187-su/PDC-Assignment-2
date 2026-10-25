
public class FIXOrderEvent extends TradeEvent {

    private static final double FEE_RATE = 0.0015;
    private final String clOrdId;

    public FIXOrderEvent(String senderId,
                         long timestamp,
                         double basePrice,
                         String clOrdId) {

        super(senderId, timestamp, basePrice);

        if (clOrdId == null || clOrdId.isBlank()) {
            throw new IllegalArgumentException(
                "Client Order ID cannot be empty."
            );
        }

        this.clOrdId = clOrdId;
    }

    public double calculateFee() {
        return basePrice * FEE_RATE;
    }

    public double calculateTotalCost() {
        return basePrice + calculateFee();
    }

    @Override
    public void dispatch() {

        System.out.println("===== FIX Order Event =====");
        System.out.println("Sender ID: " + senderId);
        System.out.println("Timestamp: " + timestamp);
        System.out.println("Client Order ID: " + clOrdId);

        System.out.printf("Base Price: $%.2f%n", basePrice);
        System.out.printf("Transaction Fee: $%.2f%n",
                          calculateFee());
        System.out.printf("Total Cost: $%.2f%n",
                          calculateTotalCost());
    }
}
