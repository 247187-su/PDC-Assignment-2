
public abstract class TradeEvent {

    protected final String senderId;
    protected final long timestamp;
    protected final double basePrice;

    public TradeEvent(String senderId, long timestamp,
                      double basePrice) {

        if (senderId == null || senderId.isBlank()) {
            throw new IllegalArgumentException(
                "Sender ID cannot be empty."
            );
        }

        if (timestamp <= 0) {
            throw new IllegalArgumentException(
                "Timestamp must be positive."
            );
        }

        if (!Double.isFinite(basePrice) || basePrice <= 0) {
            throw new IllegalArgumentException(
                "Invalid base price."
            );
        }

        this.senderId = senderId;
        this.timestamp = timestamp;
        this.basePrice = basePrice;
    }

    public abstract void dispatch();
}
