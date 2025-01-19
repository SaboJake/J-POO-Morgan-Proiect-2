package org.poo.commands;

import lombok.Getter;
import lombok.Setter;
import org.poo.actors.Account;
import org.poo.banking.BusinessTransactionReport;
import org.poo.fileio.CommandInput;
import org.poo.outputs.DefaultOutput;
import org.poo.utils.Maps;

@Setter @Getter
public class BusinessReport extends BankCommand implements Command {
    private String type;
    private int startTimestamp;
    private int endTimestamp;
    private String iban;

    public BusinessReport(final CommandInput input) {
        this.setTimestamp(input.getTimestamp());
        this.setCommand(input.getCommand());
        this.type = input.getType();
        this.iban = input.getAccount();
        this.startTimestamp = input.getStartTimestamp();
        this.endTimestamp = input.getEndTimestamp();
    }

    /**
     * Display business report for an account
     */
    @Override
    public void execute() {
        Account account = Maps.ACCOUNT_MAP.get(iban);
        if (account == null) {
            output = new DefaultOutput(timestamp, "Account not found");
            return;
        }
        if (!account.getType().equals("business")) {
            output = new DefaultOutput(timestamp,
                    "This kind of report is not supported");
        } else {
            if (type.equals("transaction")) {
                output = new BusinessTransactionReport(account, startTimestamp, endTimestamp);
            }
        }
    }
}
