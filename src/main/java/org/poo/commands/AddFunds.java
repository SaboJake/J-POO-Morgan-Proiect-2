package org.poo.commands;

import lombok.Getter;
import lombok.Setter;
import org.poo.actors.Account;
import org.poo.exceptions.NegativeAddedFundsException;
import org.poo.exceptions.NoAccountException;
import org.poo.exceptions.NotAssociateException;
import org.poo.exceptions.NotAuthorizedException;
import org.poo.fileio.CommandInput;
import org.poo.transactions.AddFundsTransaction;
import org.poo.utils.Maps;

@Setter @Getter
public class AddFunds extends BankCommand implements Command {
    private String iban;
    private double amount;
    private String email;

    public AddFunds(final CommandInput input) {
        super();
        if (input.getAlias() != null) {
            this.iban = Maps.ALIAS_MAP.get(input.getAlias());
        } else {
            this.iban = input.getAccount();
        }
        this.amount = input.getAmount();
        this.email = input.getEmail();
    }
    /**
     * Add funds
     */
    @Override
    public void execute() throws NoAccountException, NegativeAddedFundsException {
        Account account = Maps.ACCOUNT_MAP.get(iban);
        if (account == null) {
            throw new NoAccountException(iban);
        }
        if (amount < 0) {
            throw new NegativeAddedFundsException(amount + "");
        }
        if (!account.getType().equals("business")) {
            account.setBalance(account.getBalance() + amount);
            return;
        }
        if (!account.getBusinessAccount().isAssociate(email)) {
            throw new NotAssociateException(email);
        }
        if (!account.getBusinessAccount().canDeposit(email, amount)) {
            throw new NotAuthorizedException(email);
        }
        // System.out.println(email + " added " + amount + " to " + iban);
        account.setBalance(account.getBalance() + amount);
        AddFundsTransaction tr = new AddFundsTransaction(timestamp, "Added funds", amount, email);
        account.getBusinessAccount().getAddFundsTransactions().add(tr);
    }
}
