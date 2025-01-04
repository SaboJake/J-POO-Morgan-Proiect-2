package org.poo.commands;

import lombok.Getter;
import lombok.Setter;
import org.poo.actors.Account;
import org.poo.actors.Card;
import org.poo.actors.User;
import org.poo.actors.cardState.FrozenState;
import org.poo.exceptions.NoCardException;
import org.poo.fileio.CommandInput;
import org.poo.transactions.Transaction;
import org.poo.utils.Maps;

@Setter @Getter
public class CheckCardStatus extends BankCommand implements Command {
    private String cardNumber;
    private static final int WARNING_THRESHOLD = 30;

    public CheckCardStatus(final CommandInput input) {
        this.setCommand(input.getCommand());
        this.setTimestamp(input.getTimestamp());
        this.cardNumber = input.getCardNumber();
    }

    /**
     * Check if account balance is smaller that minBalance and set card status accordingly
     */
    @Override
    public void execute() {
        Card card = Maps.CARD_MAP.get(cardNumber);
        if (card == null) {
            throw new NoCardException(cardNumber);
        }
        Account account = Maps.ACCOUNT_MAP.get(cardNumber);
        if (account.getBalance() == 0) {
            User user = Maps.USER_MAP.get(cardNumber);
            Transaction tr = new Transaction(timestamp,
                    "You have reached the minimum amount of funds, the card will be frozen");
            user.getTransactions().add(tr);
            account.getTransactions().add(tr);
            card.setStatus("frozen");
            card.setState(new FrozenState(timestamp, "The card is frozen"));
        }
        if (account.getMinBalance() == -1 || account.getMinBalance() <= account.getBalance()) {
            return;
        }
        if (account.getMinBalance() - account.getBalance() < WARNING_THRESHOLD) {
            card.setStatus("warning");
        } else {
            card.setStatus("frozen");
        }
    }
}
