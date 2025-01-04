package org.poo.commands;

import lombok.Getter;
import lombok.Setter;
import org.poo.actors.Account;
import org.poo.exceptions.NoAccountException;
import org.poo.fileio.CommandInput;
import org.poo.utils.Maps;

@Setter @Getter
public class SetMinBalance extends BankCommand implements Command {
    private double amount;
    private String iban;

    public SetMinBalance(final CommandInput input) {
        this.setCommand(input.getCommand());
        this.setTimestamp(input.getTimestamp());
        this.amount = input.getAmount();
        this.iban = input.getAccount();
    }

    /**
     * Set minimum balance for an account
     */
    @Override
    public void execute() {
        Account account = Maps.ACCOUNT_MAP.get(iban);
        if (account == null) {
            throw new NoAccountException(iban);
        }
        account.setMinBalance(amount);
    }
}
