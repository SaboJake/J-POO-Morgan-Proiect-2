package org.poo.commands;

import lombok.Getter;
import lombok.Setter;
import org.poo.actors.Account;
import org.poo.actors.Commerciant;
import org.poo.actors.User;
import org.poo.exceptions.InsufficientFundsException;
import org.poo.exceptions.NoAccountException;
import org.poo.exceptions.NotAuthorizedException;
import org.poo.fileio.CommandInput;
import org.poo.transactions.SendMoneyTransaction;
import org.poo.transactions.Transaction;
import org.poo.utils.CommandUtils;
import org.poo.utils.DiscountUtil;
import org.poo.utils.Maps;

@Setter @Getter
public class SendMoney extends BankCommand implements Command {
    private String fromIban;
    private String toIban;
    private double amount;
    private String description;
    private String email;

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
        this.email = input.getEmail();
    }

    /**
     * Makes bank transfer from one account to another
     */
    @Override
    public void execute() throws NoAccountException, InsufficientFundsException {
        Account fromAccount = Maps.ACCOUNT_MAP.get(fromIban);
        Account toAccount = Maps.ACCOUNT_MAP.get(toIban);
        Commerciant comm = Maps.COMM_ACCOUNT_MAP.get(toIban);
        if (fromAccount == null) {
            throw new NoAccountException(fromIban);
        }
        if (toAccount == null && comm == null) {
            throw new NoAccountException(toIban);
        }
        User user = Maps.USER_MAP.get(fromIban);
        double commission = CommandUtils
                .getCommission(amount, user.getServicePlan(), fromAccount.getCurrency());

        if (fromAccount.getType().equals("business")) {
            if (!fromAccount.getBusinessAccount().canSpend(email, amount + commission)) {
                throw new NotAuthorizedException(email);
            }
        }

        if (fromAccount.getBalance() < amount + commission) {
            System.out.println("Had: " + fromAccount.getBalance() + " Tried to send: "
                    + amount + " + " + commission);
            throw new InsufficientFundsException(fromIban);
        }

        fromAccount.setBalance(fromAccount.getBalance() - amount - commission);
        Transaction updatePlan;
        // Check if transaction is being made to a commerciant
        if (comm != null) {
            // Add transaction based on commerciant
            updatePlan = DiscountUtil
                    .discountLogic(fromAccount, amount, comm.getCommerciant(), timestamp);
            // Add transaction
            amount = CommandUtils.round(amount, PLACES);
            SendMoneyTransaction tr1 = new SendMoneyTransaction(timestamp, description,
                    fromIban, toIban,
                    amount + " " + fromAccount.getCurrency(), "sent");
            User from = Maps.USER_MAP.get(fromIban);
            from.getTransactions().add(tr1);
            fromAccount.getTransactions().add(tr1);
            return;
        }
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
        if (fromAccount.getType().equals("business")) {
            tr1.setEmail(email);
        }
        from.getTransactions().add(tr1);
        fromAccount.getTransactions().add(tr1);

        SendMoneyTransaction tr2 = new SendMoneyTransaction(timestamp, description,
                fromIban, toIban,
                actualAmount + " " + toAccount.getCurrency(), "received");
        to.getTransactions().add(tr2);
        toAccount.getTransactions().add(tr2);
        // maybe add updatePlan???
    }
}
