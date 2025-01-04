package org.poo.commands;

import lombok.Getter;
import lombok.Setter;
import org.poo.actors.Account;
import org.poo.actors.User;
import org.poo.exceptions.InsufficientFundsException;
import org.poo.exceptions.NoAccountException;
import org.poo.fileio.CommandInput;
import org.poo.transactions.SendMoneyTransaction;
import org.poo.utils.CommandUtils;
import org.poo.utils.Maps;

@Setter @Getter
public class SendMoney extends BankCommand implements Command {
    private String fromIban;
    private String toIban;
    private double amount;
    private String description;

    private static final int PLACES = 5;

    public SendMoney(final CommandInput input) {
        this.setCommand(input.getCommand());
        this.setTimestamp(input.getTimestamp());
        this.fromIban = input.getAccount();
        this.toIban = input.getReceiver();
        if (Maps.ALIAS_MAP.containsKey(toIban)) {
            this.toIban = Maps.ALIAS_MAP.get(toIban);
        }
        this.amount = input.getAmount();
        this.description = input.getDescription();
    }

    /**
     * Makes bank transfer from one account to another
     */
    @Override
    public void execute() throws NoAccountException, InsufficientFundsException {
        Account fromAccount = Maps.ACCOUNT_MAP.get(fromIban);
        Account toAccount = Maps.ACCOUNT_MAP.get(toIban);
        if (fromAccount == null) {
            throw new NoAccountException(fromIban);
        }
        if (toAccount == null) {
            throw new NoAccountException(toIban);
        }
        if (fromAccount.getBalance() < amount) {
            System.out.println("Had: " + fromAccount.getBalance() + " Tried to send: " + amount);
            throw new InsufficientFundsException(fromIban);
        }

        fromAccount.setBalance(fromAccount.getBalance() - amount);
        double actualAmount = CommandUtils.getActualAmount(amount,
                fromAccount.getCurrency(), toAccount.getCurrency());
        toAccount.setBalance(toAccount.getBalance() + actualAmount);

        // add transactions
        amount = CommandUtils.round(amount, PLACES);
        actualAmount = CommandUtils.round(actualAmount, PLACES);
        User from = Maps.USER_MAP.get(fromIban);
        User to = Maps.USER_MAP.get(toIban);

        SendMoneyTransaction tr1 = new SendMoneyTransaction(timestamp, description,
                fromIban, toIban,
                amount + " " + fromAccount.getCurrency(), "sent");
        from.getTransactions().add(tr1);
        fromAccount.getTransactions().add(tr1);

        SendMoneyTransaction tr2 = new SendMoneyTransaction(timestamp, description,
                fromIban, toIban,
                actualAmount + " " + toAccount.getCurrency(), "received");
        to.getTransactions().add(tr2);
        toAccount.getTransactions().add(tr2);
    }
}
