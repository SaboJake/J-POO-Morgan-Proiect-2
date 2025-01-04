package org.poo.commands;

import lombok.Getter;
import lombok.Setter;
import org.poo.actors.Account;
import org.poo.banking.AccountReport;
import org.poo.exceptions.NoAccountException;
import org.poo.fileio.CommandInput;
import org.poo.outputs.DefaultOutput;
import org.poo.utils.Maps;

@Setter @Getter
public class Report extends BankCommand implements Command {
    private final int startTimestamp;
    private final int endTimestamp;
    private final String iban;

    public Report(final CommandInput input) {
        this.setCommand(input.getCommand());
        this.setTimestamp(input.getTimestamp());
        this.startTimestamp = input.getStartTimestamp();
        this.endTimestamp = input.getEndTimestamp();
        this.iban = input.getAccount();
    }

    /**
     * Displays report for an account
     */
    @Override
    public void execute() throws NoAccountException {
        Account account = Maps.ACCOUNT_MAP.get(iban);
        if (account == null) {
            output = new DefaultOutput(timestamp, "Account not found");
        } else {
            output = new AccountReport(account, startTimestamp, endTimestamp);
        }
    }
}
