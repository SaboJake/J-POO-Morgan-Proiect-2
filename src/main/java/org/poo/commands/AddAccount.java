package org.poo.commands;

import lombok.Getter;
import lombok.Setter;
import org.poo.actors.Account;
import org.poo.actors.AccountBuilder;
import org.poo.actors.BusinessAccount;
import org.poo.actors.User;

import org.poo.exceptions.NoUserException;
import org.poo.fileio.CommandInput;
import org.poo.transactions.Transaction;
import org.poo.utils.Maps;
import org.poo.utils.Utils;

@Setter @Getter
public class AddAccount extends BankCommand implements Command {
    private String email;
    private String currency;
    private String type;
    private double interestRate;

    public AddAccount(final CommandInput input) {
        this.setCommand(input.getCommand());
        this.setTimestamp(input.getTimestamp());
        this.email = input.getEmail();
        this.currency = input.getCurrency();
        this.type = input.getAccountType();
        this.interestRate = input.getInterestRate();
    }

    /**
     * Creates new account for the user
     */
    @Override
    public void execute() throws NoUserException {
        User user = Maps.USER_MAP.get(email);
        if (user == null) {
            throw new NoUserException(email);
        }
        String iban = Utils.generateIBAN();
        Account account = new AccountBuilder(iban, 0.0, currency, type)
                .withInterestRate(interestRate)
                .build();
        if (type.equals("business")) {
            account.setBusinessAccount(new BusinessAccount(email, account));
            account.getBusinessAccount().setAccount(account);
        }
        user.getAccounts().add(account);

        // iban -> account
        Maps.ACCOUNT_MAP.put(iban, user.getAccounts().getLast());
        // iban -> user
        Maps.USER_MAP.put(iban, user);
        // currency -> account
        if (account.getType().equals("classic")
                && !user.getCurrencyAccountMap().containsKey(currency)) {
            user.getCurrencyAccountMap().put(currency, user.getAccounts().getLast());
        }
        // add transaction
        user.getTransactions().add(new Transaction(timestamp, "New account created"));
        account.getTransactions().add(new Transaction(timestamp, "New account created"));
    }
}
