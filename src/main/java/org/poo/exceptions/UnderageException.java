package org.poo.exceptions;

import org.poo.actors.Account;
import org.poo.actors.User;
import org.poo.commands.BankCommand;
import org.poo.transactions.Transaction;
import org.poo.utils.Maps;

public class UnderageException extends NoOutputNecessaryException {
    public UnderageException(final String message) {
        super(message);
    }

    /**
     * Handle underage exception
     * @param command - the command
     */
    @Override
    public void handle(final BankCommand command) {
        System.out.println("You are underage: " + getMessage());
        Transaction transaction = new Transaction(command.getTimestamp(),
                "You don't have the minimum age required.");
        User user = Maps.USER_MAP.get(getMessage());
        Account account = Maps.ACCOUNT_MAP.get(getMessage());
        user.getTransactions().add(transaction);
        account.getTransactions().add(transaction);
    }
}
