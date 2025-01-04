package org.poo.commands;

import lombok.Getter;
import lombok.Setter;
import org.poo.actors.Account;
import org.poo.exceptions.NegativeAddedFundsException;
import org.poo.exceptions.NoAccountException;
import org.poo.fileio.CommandInput;
import org.poo.utils.Maps;

@Setter @Getter
public class AddFunds extends BankCommand implements Command {
    private String iban;
    private double amount;

    public AddFunds(final CommandInput input) {
        super();
        if (input.getAlias() != null) {
            this.iban = Maps.ALIAS_MAP.get(input.getAlias());
        } else {
            this.iban = input.getAccount();
        }
        this.amount = input.getAmount();
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
        account.setBalance(account.getBalance() + amount);
    }
}
