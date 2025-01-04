package org.poo.commands;

import lombok.Getter;
import lombok.Setter;
import org.poo.actors.Account;
import org.poo.actors.Card;
import org.poo.actors.User;
import org.poo.exceptions.NoCardException;
import org.poo.fileio.CommandInput;
import org.poo.transactions.CardTransaction;
import org.poo.utils.Maps;

@Setter @Getter
public class DeleteCard extends BankCommand implements Command {
    private String cardNumber;

    public DeleteCard(final CommandInput input) {
        this.setCommand(input.getCommand());
        this.setTimestamp(input.getTimestamp());
        this.cardNumber = input.getCardNumber();
    }

    /**
     * Set status of card give by cardNumber as INACTIVE
     */
    @Override
    public void execute() throws NoCardException {
        Card card = Maps.CARD_MAP.get(cardNumber);
        if (card == null) {
            throw new NoCardException(cardNumber);
        }
        // Add transaction
        User user = Maps.USER_MAP.get(cardNumber);
        Account account = Maps.ACCOUNT_MAP.get(cardNumber);
        CardTransaction tr = new CardTransaction(timestamp, "The card has been destroyed",
                cardNumber, user.getEmail(), account.getIban());
        user.getTransactions().add(tr);
        account.getTransactions().add(tr);
        account.getCards().remove(card);
        Maps.CARD_MAP.remove(cardNumber);
        Maps.ACCOUNT_MAP.remove(cardNumber);
        Maps.USER_MAP.remove(cardNumber);
    }
}
