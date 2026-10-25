
public class TradingEngineApp {

    public static void main(String[] args) {

        System.out.println(
            "===== Sultan's Trading Engine ====="
        );

        System.out.println(
            "Student: Sultan Majeed | Reg ID: 247187"
        );

        long timestamp = System.currentTimeMillis();

        // Store different trading events in one array
        TradeEvent[] events = {

            new FIXOrderEvent(
                "FIX-SENDER-01",
                timestamp,
                1000.00,
                "ORD-247187"
            ),

            new ReutersQuoteEvent(
                "REUTERS-SENDER-01",
                timestamp,
                150.00,
                149.50,
                150.25
            )
        };

        // Runtime polymorphism
        for (TradeEvent event : events) {

            System.out.println();

            event.dispatch();

            System.out.println(
                "------------------------------"
            );
        }

        System.out.println(
            "\n===== Trading Engine Completed ====="
        );
    }
}
