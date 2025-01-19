package org.poo.banking;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import org.poo.actors.Account;
import org.poo.actors.User;
import org.poo.transactions.PayOnlineTransaction;
import org.poo.transactions.SendMoneyTransaction;
import org.poo.transactions.Transaction;
import org.poo.utils.Maps;
import org.poo.utils.RoundingSerializer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

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
        // Filter only sendMoney and payOnline transactions
        transactions.removeIf(tr -> !tr.getType().equals("sendMoney")
                && !tr.getType().equals("payOnline"));
        Map<String, CommerciantBusinessSpending> spendingMap = new HashMap<>();
        for (Transaction tr: transactions) {
            String email = "", commerciant = "";
            double amount = -1;
            // Getting the email, amount and commerciant
            if (tr.getType().equals("payOnline")) {
                email = ((PayOnlineTransaction) tr).getEmail();
                amount = ((PayOnlineTransaction) tr).getAmount();
                commerciant = ((PayOnlineTransaction) tr).getCommerciant();
            } else {
                if (Maps.COMM_MAP.containsKey(((SendMoneyTransaction) tr).getReceiverIBAN())) {
                    email = ((SendMoneyTransaction) tr).getEmail();
                    amount = Double.parseDouble(((SendMoneyTransaction) tr).getAmount());
                    commerciant = Maps.COMM_MAP.get(((SendMoneyTransaction) tr).getReceiverIBAN())
                            .getCommerciant();
                }
            }
            if (email.isEmpty() || amount == -1 || commerciant.isEmpty()
                    || !account.getBusinessAccount().isManager(email)
                    && !account.getBusinessAccount().isEmployee(email)) {
                continue;
            }
            if (!spendingMap.containsKey(commerciant)) {
                CommerciantBusinessSpending spending =
                        new CommerciantBusinessSpending(commerciant, amount);
                if (account.getBusinessAccount().isEmployee(email)) {
                    spending.getEmployeeEmails().add(email);
                } else if (account.getBusinessAccount().isManager(email)) {
                    spending.getManagerEmails().add(email);
                }
                spendingMap.put(commerciant, spending);
            } else {
                CommerciantBusinessSpending spending = spendingMap.get(commerciant);
                spending.setTotalReceived(spending.getTotalReceived() + amount);
                if (account.getBusinessAccount().isEmployee(email)) {
                    spending.getEmployeeEmails().add(email);
                } else if (account.getBusinessAccount().isManager(email)) {
                    spending.getManagerEmails().add(email);
                }
            }
        }
        // Go through the map
        for (CommerciantBusinessSpending spending: spendingMap.values()) {
            for (String email: spending.getManagerEmails()) {
                User user = Maps.USER_MAP.get(email);
                spending.getManagers().add(user.getLastName() + " " + user.getFirstName());
            }
            for (String email: spending.getEmployeeEmails()) {
                User user = Maps.USER_MAP.get(email);
                spending.getEmployees().add(user.getLastName() + " " + user.getFirstName());
            }
        }
        this.commerciants = new ArrayList(List.copyOf(spendingMap.values()));
        this.commerciants.sort(Comparator.comparing(CommerciantBusinessSpending::getCommerciant));
        transactions = null;
    }
}
