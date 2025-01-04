package org.poo.actors.cardState;

import org.poo.actors.Card;

public interface CardState {

    /**
     * Handles the card
     * @param card - the card to be handled
     */
    void handle(Card card);
}
