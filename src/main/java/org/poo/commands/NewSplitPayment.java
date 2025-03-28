package org.poo.commands;

import lombok.Getter;
import lombok.Setter;
import org.poo.actors.Account;
import org.poo.actors.User;
import org.poo.exceptions.NoAccountException;
import org.poo.fileio.CommandInput;
import org.poo.transactions.NewSplitPaymentTransaction;
import org.poo.transactions.Transaction;
import org.poo.utils.CommandUtils;
import org.poo.utils.Maps;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Setter @Getter
public class NewSplitPayment extends BankCommand implements Command {
    private String type;
    private List<String> ibans;
    private double totalAmount;
    private List<Double> amounts;
    private String currency;
    private String error = "";

    private Set<String> users = new HashSet<>();

    public NewSplitPayment(final CommandInput input) {
        this.setCommand(input.getCommand());
        this.setTimestamp(input.getTimestamp());
        this.type = input.getSplitPaymentType();
        this.ibans = input.getAccounts();
        this.totalAmount = input.getAmount();
        this.currency = input.getCurrency();
        if (type.equals("custom")) {
            this.amounts = input.getAmountForUsers();
        } else if (type.equals("equal")) {
            double equalAmount = totalAmount / ibans.size();
            this.amounts = IntStream.range(0, ibans.size())
                    .mapToObj(i -> equalAmount)
                    .collect(Collectors.toList());
        }
    }

    /**
     * Check if all users accepted the payment
     * @return true if all users accepted the payment
     */
    public boolean isAccepted() {
        return users.size() == ibans.size();
    }

    /**
     * Check if all accounts exist, add the payment to the users
     */
    @Override
    public void execute() {
        for (String iban: ibans) {
            if (!Maps.ACCOUNT_MAP.containsKey(iban)) {
                throw new NoAccountException(iban);
            }
        }
        for (String iban: ibans) {
            User user = Maps.USER_MAP.get(iban);
            user.getSplitPayments().addLast(this);
        }
    }

    private void addSorted(final List<Transaction> list, final Transaction tr) {
        ListIterator<Transaction> it = list.listIterator();
        while (it.hasNext()) {
            if (it.next().getTimestamp() > tr.getTimestamp()) {
                it.previous();
                it.add(tr);
                return;
            }
        }
        list.add(tr);
    }

    /**
     * Initiate the payment when everyone accepts
     */
    public void initiate() {
        List<Double> actualAmounts = new ArrayList<>();
        List<Double> commissions = new ArrayList<>();
        for (String iban: ibans) {
            Account account = Maps.ACCOUNT_MAP.get(iban);
            double actualAmount = CommandUtils.getActualAmount(amounts.get(ibans.indexOf(iban)),
                    currency, account.getCurrency());
            double commission = CommandUtils.getCommission(actualAmount,
                    Maps.USER_MAP.get(iban).getServicePlan(), account.getCurrency());
            commission = 0;
            if (account.getBalance() < actualAmount + commission && error.equals("")) {
                error = "Account " + iban + " has insufficient funds for a split payment.";
                break;
            }
            actualAmounts.add(actualAmount);
            commissions.add(commission);
        }
        NewSplitPaymentTransaction tr = getNewSplitPaymentTransaction(error);

        for (String iban: ibans) {
            Account account = Maps.ACCOUNT_MAP.get(iban);
            User user = Maps.USER_MAP.get(iban);
            addSorted(user.getTransactions(), tr);
            addSorted(account.getTransactions(), tr);
        }

        if (!error.isEmpty()) {
            return;
        }
        for (String iban: ibans) {
            Account account = Maps.ACCOUNT_MAP.get(iban);
            double actualAmount = actualAmounts.get(ibans.indexOf(iban));
            double commission = commissions.get(ibans.indexOf(iban));
            account.setBalance(account.getBalance() - actualAmount - commission);
        }
    }

    private NewSplitPaymentTransaction getNewSplitPaymentTransaction(final String err) {
        String description = "Split payment of "
                + String.format("%.2f", totalAmount) + " " + currency;
        NewSplitPaymentTransaction tr = new NewSplitPaymentTransaction(timestamp,
                description, ibans, currency, type);
        if (type.equals("custom")) {
            tr.setAmountForUsers(amounts);
        } else if (type.equals("equal")) {
            tr.setAmount(totalAmount / ibans.size());
        }
        if (!err.isEmpty()) {
            tr.setError(err);
        }
        return tr;
    }

    /**
     * Remove the payment from the users
     */
    public void remove() {
        for (String iban: ibans) {
            User user = Maps.USER_MAP.get(iban);
            user.getSplitPayments().remove(this);
        }
    }
}
