package org.poo.actors.cardState;

import org.poo.actors.Card;

public class WarningState implements CardState {
    /**
     * Handles the card - behaves like a regular card
     * @param card - the card to be handled
     */
    @Override
    public void handle(final Card card) {
        System.out.println("Warning: The card is about to be frozen");
    }
}
