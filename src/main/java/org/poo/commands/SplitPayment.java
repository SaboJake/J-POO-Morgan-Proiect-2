package org.poo.commands;

import lombok.Getter;
import lombok.Setter;
import org.poo.actors.Account;
import org.poo.actors.User;
import org.poo.banking.ExchangeRate;
import org.poo.exceptions.InsufficientFundsException;
import org.poo.exceptions.NoAccountException;
import org.poo.fileio.CommandInput;
import org.poo.transactions.SplitPaymentTransaction;
import org.poo.utils.CommandUtils;
import org.poo.utils.Maps;

import java.util.List;
import java.util.ArrayList;

@Setter @Getter
public class SplitPayment extends BankCommand implements Command {
    private List<String> accounts;
    private String currency;
    private double amount;

    public SplitPayment(final CommandInput input) {
        this.setCommand(input.getCommand());
        this.setTimestamp(input.getTimestamp());
        this.accounts = new ArrayList<>(input.getAccounts());
        this.currency = input.getCurrency();
        this.amount = input.getAmount();
    }

    private SplitPaymentTransaction tr;

    /**
     * Make split payment
     */
    @Override
    public void execute() throws NoAccountException, InsufficientFundsException {
        final int upgradeThreshold = 300;
        // Preemptively set transaction
        String description = "Split payment of " + String.format("%.2f", amount) + " " + currency;
        amount /= accounts.size();
        tr = new SplitPaymentTransaction(timestamp, description, currency, amount, accounts);
        List<Double> amounts = new ArrayList<>();
        for (int i = accounts.size() - 1; i >= 0; i--) {
            amounts.add(CommandUtils.bankTransferCheck(accounts.get(i), currency, amount));
        }
        amounts = new ArrayList<>(amounts.reversed());
        for (int i = 0; i < amounts.size(); i++) {
            Account account = Maps.ACCOUNT_MAP.get(accounts.get(i));
            account.setBalance(account.getBalance() - amounts.get(i));
            // add transaction
            User user = Maps.USER_MAP.get(accounts.get(i));
            user.getTransactions().add(tr);
            account.getTransactions().add(tr);
            // update service plan
            double ronAmount = ExchangeRate.getRONRate(currency, amounts.get(i));
            if (ronAmount >= upgradeThreshold) {
                user.updateProgress(account, timestamp);
            }
        }
    }
}
