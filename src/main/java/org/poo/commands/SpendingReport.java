package org.poo.commands;

import org.poo.actors.Account;
import org.poo.banking.SpendReport;
import org.poo.fileio.CommandInput;
import org.poo.outputs.DefaultOutput;
import org.poo.outputs.JustErrorOutput;
import org.poo.utils.Maps;

public class SpendingReport extends Report {
    public SpendingReport(final CommandInput input) {
        super(input);
    }

    /**
     * Display spending report for an account
     */
    @Override
    public void execute() {
        Account account = Maps.ACCOUNT_MAP.get(getIban());
        if (account == null) {
            output = new DefaultOutput(timestamp, "Account not found");
            return;
        }
        if (account.getType().equals("savings")) {
            output = new JustErrorOutput("This kind of report is"
                    + " not supported for a saving account");
        } else {
            output = new SpendReport(account, getStartTimestamp(), getEndTimestamp());
        }
    }
}
