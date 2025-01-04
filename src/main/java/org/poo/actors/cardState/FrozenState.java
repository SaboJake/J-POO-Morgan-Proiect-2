package org.poo.actors.cardState;

import lombok.AllArgsConstructor;
import lombok.Setter;
import org.poo.actors.Account;
import org.poo.actors.Card;
import org.poo.actors.User;
import org.poo.utils.Maps;
import org.poo.transactions.Transaction;

@AllArgsConstructor @Setter
public class FrozenState implements CardState {
    private int timestamp;
    private String message = "The card is frozen";
    /**
     * Handle frozen card; add transaction to user and account
     * @param card - the card to be handled
     */
    @Override
    public void handle(final Card card) {
        User user = Maps.USER_MAP.get(card.getCardNumber());
        Account account = Maps.ACCOUNT_MAP.get(card.getCardNumber());

        user.getTransactions().add(new Transaction(timestamp, message));
        account.getTransactions().add(new Transaction(timestamp, message));
    }
}
