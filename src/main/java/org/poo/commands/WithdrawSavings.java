package org.poo.commands;

import lombok.Getter;
import lombok.Setter;

import org.poo.actors.Account;
import org.poo.actors.User;
import org.poo.banking.CurrencyPair;
import org.poo.banking.ExchangeRate;
import org.poo.exceptions.*;
import org.poo.fileio.CommandInput;
import org.poo.transactions.WithdrawSavingsTransaction;
import org.poo.utils.CommandUtils;
import org.poo.utils.Maps;

@Setter @Getter
public class WithdrawSavings extends BankCommand implements Command {
    private String savingsAccount;
    private double amount;
    private String currency;

    public WithdrawSavings(final CommandInput input) {
        this.setCommand(input.getCommand());
        this.setTimestamp(input.getTimestamp());
        this.savingsAccount = input.getAccount();
        this.amount = input.getAmount();
        this.currency = input.getCurrency();
    }

    /**
     * Withdraws money from a savings account
     */
    @Override
    public void execute() {
        final int minAge = 21;
        Account fromAccount = Maps.ACCOUNT_MAP.get(savingsAccount);
        // Account not found
        if (fromAccount == null) {
            throw new NoAccountException(savingsAccount);
        }
        User user = Maps.USER_MAP.get(savingsAccount);
        Account toAccount = user.getCurrencyAccountMap().get(currency);
        // You do not have a classic account
        if (toAccount == null) {
            throw new NoClassicAccountException(savingsAccount);
        }
        // Account is not of type savings
        if (!fromAccount.getType().equals("savings")) {
            throw new NotSavingsAccountException(savingsAccount);
        }
        // You don't have the minimum age required
        if (user.getAge() < minAge) {
            throw new UnderageException(savingsAccount);
        }

        // Money withdrawn (actualAmount) = amount from "currency" converted
        // to the currency of the savings account
        double actualAmount = amount;
        if (!currency.equals(fromAccount.getCurrency())) {
            double r = ExchangeRate.DIST.get(new CurrencyPair(currency, fromAccount.getCurrency()));
            actualAmount *= r;
        }
        double commission = CommandUtils.getCommission(actualAmount, user.getServicePlan(),
                fromAccount.getCurrency());
        commission = 0;

        // Insufficient funds
        if (fromAccount.getBalance() < actualAmount + commission) {
            throw new InsufficientFundsException(savingsAccount);
        }
        fromAccount.setBalance(fromAccount.getBalance() - actualAmount - commission);
        toAccount.setBalance(toAccount.getBalance() + actualAmount);
        // Add transaction
        WithdrawSavingsTransaction tr = new WithdrawSavingsTransaction(this.getTimestamp(),
                "Savings withdrawal", toAccount.getIban(), savingsAccount, actualAmount);
        fromAccount.getTransactions().add(tr);
        toAccount.getTransactions().add(tr);
        user.getTransactions().add(tr);
        user.getTransactions().add(tr);
    }
}
