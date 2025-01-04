package org.poo.exceptions;

import org.poo.actors.Account;
import org.poo.actors.User;
import org.poo.commands.BankCommand;
import org.poo.commands.SplitPayment;
import org.poo.transactions.SplitPaymentErrorTransaction;
import org.poo.transactions.SplitPaymentTransaction;
import org.poo.transactions.Transaction;
import org.poo.utils.Maps;

public class InsufficientFundsException extends NoOutputNecessaryException {
    public InsufficientFundsException(final String message) {
        super(message);
    }

    /**
     * Handle insufficient funds exception for split payment and other cases
     * @param command - the command that caused the exception
     */
    @Override
    public void handle(final BankCommand command) {
        System.out.println("Insufficient funds: " + getMessage());
        if (command.getCommand().equals("splitPayment")) {
            SplitPaymentTransaction tr = ((SplitPayment) command).getTr();
            String error = "Account " + getMessage()
                    + " has insufficient funds for a split payment.";
            SplitPaymentErrorTransaction etr = new SplitPaymentErrorTransaction(tr, error);
            for (String account: ((SplitPayment) command).getAccounts()) {
                User user = Maps.USER_MAP.get(account);
                Account account1 = Maps.ACCOUNT_MAP.get(account);
                user.getTransactions().add(etr);
                account1.getTransactions().add(etr);
            }
        } else {
            User user = Maps.USER_MAP.get(getMessage());
            Account account = Maps.ACCOUNT_MAP.get(getMessage());
            user.getTransactions().add(new Transaction(command.getTimestamp(),
                    "Insufficient funds"));
            account.getTransactions().add(new Transaction(command.getTimestamp(),
                    "Insufficient funds"));
        }
        System.out.println("c: " + command.getCommand() + " t: " + command.getTimestamp());
    }
}
