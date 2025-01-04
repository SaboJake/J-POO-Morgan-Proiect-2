package org.poo.actors.cardState;

public final class CardStateFactory {
    private CardStateFactory() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Creates a card state based on the given parameters
     * @param state - the state of the card
     * @param timestamp - the timestamp of the command
     * @param amount - the amount to be handled
     * @param currency - the currency of the transaction
     * @param commerciant - the commerciant
     * @return the card state
     */
    public static CardState createCardState(final String state, final int timestamp,
                                            final double amount,
                                            final String currency, final String commerciant) {
        return switch (state) {
            case "active" -> new RegularState(timestamp, amount, currency, commerciant);
            case "frozen" -> new FrozenState(timestamp, "The card is frozen");
            case "warning" -> new WarningState();
            default -> throw new IllegalArgumentException("Invalid card state");
        };
    }
}
