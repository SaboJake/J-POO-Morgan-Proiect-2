package org.poo.commands;

import lombok.Getter;
import lombok.Setter;
import org.poo.actors.Account;
import org.poo.actors.User;
import org.poo.exceptions.NoAccountException;
import org.poo.exceptions.NotSavingsAccountException;
import org.poo.fileio.CommandInput;
import org.poo.transactions.AddInterestTransaction;
import org.poo.utils.Maps;

@Setter @Getter
public class AddInterest extends BankCommand implements Command {
    private String iban;

    public AddInterest(final CommandInput input) {
        this.setCommand(input.getCommand());
        this.setTimestamp(input.getTimestamp());
        this.iban = input.getAccount();
    }

    /**
     * Set balance to amount * (1 + interestRate)
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
        double amount = account.getInterestRate() * account.getBalance();
        account.setBalance(account.getBalance() + amount);
        // Add transaction
        AddInterestTransaction transaction = new AddInterestTransaction(
                getTimestamp(), "Interest rate income", amount, account.getCurrency());
        account.getTransactions().add(transaction);
        User user = Maps.USER_MAP.get(iban);
        user.getTransactions().add(transaction);
    }
}
