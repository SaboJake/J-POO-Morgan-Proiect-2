package org.poo.commands;

import lombok.Getter;
import lombok.Setter;
import org.poo.actors.Account;
import org.poo.exceptions.AlreadyAssociateException;
import org.poo.fileio.CommandInput;
import org.poo.utils.CommandUtils;
import org.poo.utils.Maps;

@Setter @Getter
public class AddNewBusinessAssociate extends BankCommand implements Command {
    private String iban;
    private String role;
    private String email;

    public AddNewBusinessAssociate(final CommandInput input) {
        this.setCommand(input.getCommand());
        this.setTimestamp(input.getTimestamp());
        this.iban = input.getAccount();
        this.role = input.getRole();
        this.email = input.getEmail();
    }

    /**
     * Add a new associate to the business account
     */
    @Override
    public void execute() {
        Account account = Maps.ACCOUNT_MAP.get(iban);
        CommandUtils.businessCheck(account, email);
        if (account.getBusinessAccount().getAssosciateMap().containsKey(email)
        || account.getBusinessAccount().getOwner().equals(email)) {
            throw new AlreadyAssociateException(email);
        }
        account.getBusinessAccount().addAssociate(email, role);
    }
}
