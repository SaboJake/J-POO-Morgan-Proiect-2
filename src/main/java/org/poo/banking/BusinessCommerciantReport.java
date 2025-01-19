package org.poo.banking;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import org.poo.actors.Account;
import org.poo.actors.User;
import org.poo.transactions.PayOnlineTransaction;
import org.poo.transactions.SendMoneyTransaction;
import org.poo.utils.Maps;
import org.poo.utils.RoundingSerializer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

@Setter @Getter
public class BusinessCommerciantReport extends AccountReport {
    @JsonSerialize(using = RoundingSerializer.class)
    @JsonProperty("spending limit")
    private double spendingLimit;
    @JsonSerialize(using = RoundingSerializer.class)
    @JsonProperty("deposit limit")
    private double depositLimit;
    private List<CommerciantBusinessSpending> commerciants;
    @JsonProperty("statistics type")
    private String statisticsType = "commerciant";

    public BusinessCommerciantReport(final Account account,
                                     final int startTimestamp, final int endTimestamp) {
        super(account, startTimestamp, endTimestamp);
        this.spendingLimit = account.getBusinessAccount().getSpendLimit();
        this.depositLimit = account.getBusinessAccount().getDepositLimit();

        transactions.removeIf(tr -> !tr.getType().equals("sendMoney")
                && !tr.getType().equals("payOnline"));

        Map<String, CommerciantBusinessSpending> spendingMap = transactions.stream()
                .filter(tr -> {
                    String email = "";
                    if (tr.getType().equals("payOnline")) {
                        email = ((PayOnlineTransaction) tr).getEmail();
                    } else if (Maps.COMM_MAP.containsKey(((SendMoneyTransaction) tr)
                            .getReceiverIBAN())) {
                        email = ((SendMoneyTransaction) tr).getEmail();
                    }
                    return !email.isEmpty() && (account.getBusinessAccount().isManager(email)
                            || account.getBusinessAccount().isEmployee(email));
                })
                .collect(Collectors.toMap(
                        tr -> {
                            if (tr.getType().equals("payOnline")) {
                                return ((PayOnlineTransaction) tr).getCommerciant();
                            } else {
                                return Maps.COMM_MAP.get(((SendMoneyTransaction) tr)
                                        .getReceiverIBAN()).getCommerciant();
                            }
                        },
                        tr -> {
                            String email = "";
                            double amount = -1;
                            if (tr.getType().equals("payOnline")) {
                                email = ((PayOnlineTransaction) tr).getEmail();
                                amount = ((PayOnlineTransaction) tr).getAmount();
                            } else {
                                email = ((SendMoneyTransaction) tr).getEmail();
                                amount = Double.parseDouble(((SendMoneyTransaction) tr)
                                        .getAmount());
                            }
                            CommerciantBusinessSpending spending = new CommerciantBusinessSpending(
                                    tr.getType().equals("payOnline")
                                            ? ((PayOnlineTransaction) tr).getCommerciant()
                                            : Maps.COMM_MAP.get(((SendMoneyTransaction) tr)
                                            .getReceiverIBAN()).getCommerciant(),
                                    amount
                            );
                            if (account.getBusinessAccount().isEmployee(email)) {
                                spending.getEmployeeEmails().add(email);
                            } else if (account.getBusinessAccount().isManager(email)) {
                                spending.getManagerEmails().add(email);
                            }
                            return spending;
                        },
                        (existing, replacement) -> {
                            existing.setTotalReceived(existing.getTotalReceived()
                                    + replacement.getTotalReceived());
                            existing.getEmployeeEmails().addAll(replacement.getEmployeeEmails());
                            existing.getManagerEmails().addAll(replacement.getManagerEmails());
                            return existing;
                        }
                ));

        spendingMap.values().forEach(spending -> {
            spending.setManagers(spending.getManagerEmails().stream()
                    .map(email -> {
                        User user = Maps.USER_MAP.get(email);
                        return user.getLastName() + " " + user.getFirstName();
                    })
                    .collect(Collectors.toList()));
            spending.setEmployees(spending.getEmployeeEmails().stream()
                    .map(email -> {
                        User user = Maps.USER_MAP.get(email);
                        return user.getLastName() + " " + user.getFirstName();
                    })
                    .collect(Collectors.toList()));
        });

        this.commerciants = new ArrayList<>(spendingMap.values());
        this.commerciants.sort(Comparator.comparing(CommerciantBusinessSpending::getCommerciant));
        transactions = null;
    }
}
