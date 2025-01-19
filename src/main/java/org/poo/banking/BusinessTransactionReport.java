package org.poo.banking;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import org.poo.actors.Account;
import org.poo.actors.User;
import org.poo.transactions.AddFundsTransaction;
import org.poo.transactions.PayOnlineTransaction;
import org.poo.transactions.SendMoneyTransaction;
import org.poo.transactions.Transaction;
import org.poo.utils.Maps;
import org.poo.utils.RoundingSerializer;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Setter @Getter
public class BusinessTransactionReport extends AccountReport {
    @JsonProperty("spending limit")
    private double spendingLimit;
    @JsonProperty("deposit limit")
    private double depositLimit;
    private List<AssociateSpending> managers;
    private List<AssociateSpending> employees;
    @JsonSerialize(using = RoundingSerializer.class)
    @JsonProperty("total spent")
    private double totalSpent;
    @JsonSerialize(using = RoundingSerializer.class)
    @JsonProperty("total deposited")
    private double totalDeposited;
    @JsonProperty("statistics type")
    private String statisticsType = "transaction";

    public BusinessTransactionReport(final Account account,
                                     final int startTimestamp, final int endTimestamp) {
        super(account, startTimestamp, endTimestamp);
        this.spendingLimit = account.getBusinessAccount().getSpendLimit();
        this.depositLimit = account.getBusinessAccount().getDepositLimit();
        // Filter only sendMoney and payOnline transactions
        transactions.removeIf(tr -> !tr.getType().equals("sendMoney")
                && !tr.getType().equals("payOnline"));

        Map<String, Double> spendingMap = transactions.stream()
                .collect(Collectors.toMap(
                        tr -> {
                            if (tr.getType().equals("sendMoney")) {
                                return ((SendMoneyTransaction) tr).getEmail();
                            } else {
                                return ((PayOnlineTransaction) tr).getEmail();
                            }
                        },
                        tr -> {
                            if (tr.getType().equals("sendMoney")) {
                                SendMoneyTransaction sTr = (SendMoneyTransaction) tr;
                                return Double.parseDouble(sTr.getAmount());
                            } else {
                                PayOnlineTransaction pTr = (PayOnlineTransaction) tr;
                                return pTr.getAmount();
                            }
                        },
                        Double::sum
                ));
        account.getBusinessAccount().updateMap(spendingMap);

        Map<String, Double> depositMap = account.getBusinessAccount()
                .getAddFundsTransactions().stream().collect(Collectors.toMap(
                        AddFundsTransaction::getEmail,
                        AddFundsTransaction::getAmount,
                        Double::sum
                ));
        account.getBusinessAccount().updateMap(depositMap);

        // Display elements from maps for debugging
        System.out.println("Spending map:");
        spendingMap.forEach((k, v) -> System.out.println(k + " " + v));
        System.out.println("Deposit map:");
        depositMap.forEach((k, v) -> System.out.println(k + " " + v));

        this.totalSpent = spendingMap.values().stream().mapToDouble(Double::doubleValue).sum();
        this.totalSpent -= spendingMap.getOrDefault(account.getBusinessAccount().getOwner(), 0.0);
        this.totalDeposited = depositMap.values().stream().mapToDouble(Double::doubleValue).sum();
        this.totalDeposited -= depositMap.getOrDefault(account.getBusinessAccount().getOwner(), 0.0);

        this.managers = account.getBusinessAccount().getManagersSpending(spendingMap, depositMap);
        this.employees = account.getBusinessAccount().getEmployeesSpending(spendingMap, depositMap);

        transactions = null;
    }
}