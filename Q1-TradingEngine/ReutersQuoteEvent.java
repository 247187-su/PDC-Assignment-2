
public class ReutersQuoteEvent extends TradeEvent {

    private final double bidPrice;
    private final double askPrice;

    public ReutersQuoteEvent(String senderId,
                             long timestamp,
                             double basePrice,
                             double bidPrice,
                             double askPrice) {

        super(senderId, timestamp, basePrice);

        if (!Double.isFinite(bidPrice) || bidPrice <= 0) {
            throw new IllegalArgumentException(
                "Bid price must be positive and finite."
            );
        }

        if (!Double.isFinite(askPrice) || askPrice <= 0) {
            throw new IllegalArgumentException(
                "Ask price must be positive and finite."
            );
        }

        if (askPrice < bidPrice) {
            throw new IllegalArgumentException(
                "Ask price cannot be below bid price."
            );
        }

        this.bidPrice = bidPrice;
        this.askPrice = askPrice;
    }

    public double calculateSpread() {
        return askPrice - bidPrice;
    }

    @Override
    public void dispatch() {

        System.out.println("===== Reuters Quote Event =====");
        System.out.println("Sender ID: " + senderId);
        System.out.println("Timestamp: " + timestamp);

        System.out.printf("Base Price: $%.2f%n", basePrice);
        System.out.printf("Bid Price: $%.2f%n", bidPrice);
        System.out.printf("Ask Price: $%.2f%n", askPrice);
        System.out.printf("Bid-Ask Spread: $%.2f%n",
                          calculateSpread());
    }
}
