package org.poo.banking;

import lombok.Getter;
import lombok.Setter;
import org.poo.actors.Account;
import org.poo.transactions.PayOnlineTransaction;
import org.poo.transactions.Transaction;

import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Setter @Getter
public class SpendReport extends AccountReport {
    private List<CommerciantSpending> commerciants;

    public SpendReport(final Account account, final int startTimestamp, final int endTimestamp) {
        super(account, startTimestamp, endTimestamp);
        transactions.removeIf(tr -> !tr.getType().equals("payOnline"));
        Map<String, Double> commerciantMap = new HashMap<String, Double>();

        for (Transaction tr : transactions) {
            PayOnlineTransaction payOnlineTransaction = (PayOnlineTransaction) tr;
            String commerciant = payOnlineTransaction.getCommerciant();
            double amount = payOnlineTransaction.getAmount();

            commerciantMap.merge(commerciant, amount, Double::sum);
        }

        commerciants = commerciantMap.entrySet().stream()
                .map(entry -> new CommerciantSpending(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(CommerciantSpending::getCommerciant))
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
