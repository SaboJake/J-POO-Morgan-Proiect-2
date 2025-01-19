package org.poo.commands;

import lombok.Getter;
import lombok.Setter;
import org.poo.actors.Account;
import org.poo.exceptions.NotAuthorizedException;
import org.poo.fileio.CommandInput;
import org.poo.utils.CommandUtils;
import org.poo.utils.Maps;

@Setter @Getter
public class ChangeSpendingLimit extends BankCommand implements Command {
    private String email;
    private String iban;
    private double amount;

    public ChangeSpendingLimit(final CommandInput input) {
        this.setCommand(input.getCommand());
        this.setTimestamp(input.getTimestamp());
        this.email = input.getEmail();
        this.iban = input.getAccount();
        this.amount = input.getAmount();
    }

    /**
     * Change the spending limit of the associate
     */
    @Override
    public void execute() {
        Account account = Maps.ACCOUNT_MAP.get(iban);
        CommandUtils.businessCheck(account, email);
        if (!account.getBusinessAccount().getOwner().equals(email)) {
            String err = "You must be owner in order to change spending limit.";
            throw new NotAuthorizedException(err);
        }
        account.getBusinessAccount().setSpendLimit(amount);
    }
}
