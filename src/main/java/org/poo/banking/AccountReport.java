package org.poo.banking;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.poo.actors.Account;
import org.poo.transactions.Transaction;
import org.poo.utils.RoundingSerializer;

import java.util.ArrayList;
import java.util.List;

@Setter @Getter @NoArgsConstructor
public class AccountReport {
    @JsonProperty("IBAN")
    private String iban;
    @JsonSerialize(using = RoundingSerializer.class)
    private double balance;
    private String currency;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected List<Transaction> transactions;

    private static int findFirstIndex(final List<Transaction> transactions, final int target) {
        int l = 0, r = transactions.size() - 1;
        while (l < r) {
            int m = l + (r - l) / 2;
            if (transactions.get(m).getTimestamp() < target) {
                l = m + 1;
            } else {
                r = m;
            }
        }
        return l;
    }

    private static int findLastIndex(final List<Transaction> transactions, final int target) {
        int l = 0, r = transactions.size() - 1;
        while (l < r) {
            int m = l + (r - l + 1) / 2;
            if (transactions.get(m).getTimestamp() > target) {
                r = m - 1;
            } else {
                l = m;
            }
        }
        return l;
    }

    public AccountReport(final Account account, final int startTimestamp, final int endTimestamp) {
        this.iban = account.getIban();
        this.balance = account.getBalance();
        this.currency = account.getCurrency();

        if (startTimestamp > account.getTransactions().getLast().getTimestamp()
                || endTimestamp < account.getTransactions().getFirst().getTimestamp()) {
            this.transactions = new ArrayList<>();
            return;
        }

        int startIndex = findFirstIndex(account.getTransactions(), startTimestamp);
        int endIndex = findLastIndex(account.getTransactions(), endTimestamp) + 1;
        this.transactions = new ArrayList<>(account.getTransactions()
                .subList(startIndex, endIndex));
    }
}
