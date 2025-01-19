package org.poo.commands;

import org.poo.actors.Account;
import org.poo.actors.User;
import org.poo.exceptions.NoAccountException;
import org.poo.exceptions.NotSavingsAccountException;
import org.poo.fileio.CommandInput;
import org.poo.outputs.DefaultOutput;
import org.poo.transactions.Transaction;
import org.poo.utils.Maps;

public class ChangeInterestRate extends BankCommand implements Command {
    private String iban;
    private double rate;

    public ChangeInterestRate(final CommandInput input) {
        this.setCommand(input.getCommand());
        this.setTimestamp(input.getTimestamp());
        this.iban = input.getAccount();
        this.rate = input.getInterestRate();
    }

    /**
     * Change interest rate of account
     */
    @Override
    public void execute() throws NotSavingsAccountException, NoAccountException {
        Account account = Maps.ACCOUNT_MAP.get(iban);
        if (account == null) {
            throw new NoAccountException(iban);
        }
        if (!account.getType().equals("savings")) {
            throw new NotSavingsAccountException(iban);
        }
        account.setInterestRate(rate);
        User user = Maps.USER_MAP.get(iban);
        Transaction tr = new Transaction(timestamp,
                "Interest rate of the account changed to " + rate);
        user.getTransactions().add(tr);
        account.getTransactions().add(tr);
    }
}
