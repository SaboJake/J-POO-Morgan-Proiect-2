package org.poo.commands;

import lombok.Getter;
import lombok.Setter;
import org.poo.fileio.CommandInput;
import org.poo.utils.CommandUtils;
import org.poo.utils.Maps;

@Setter @Getter
public class SetAlias extends BankCommand implements Command {
    private String email;
    private String alias;
    private String iban;

    public SetAlias(final CommandInput input) {
        this.setCommand(input.getCommand());
        this.setTimestamp(input.getTimestamp());
        this.email = input.getEmail();
        this.alias = input.getAlias();
        this.iban = input.getAccount();
    }

    /**
     * Links alias to account
     */
    @Override
    public void execute() {
        CommandUtils.userAccountCheck(email, iban);
        Maps.ALIAS_MAP.put(alias, iban);
    }
}
