package org.poo.actors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.poo.transactions.Transaction;
import org.poo.utils.DiscountUtil;
import org.poo.utils.Maps;
import org.poo.utils.RoundingSerializer;


import java.util.*;

@Setter @Getter @AllArgsConstructor
public class Account {
    @JsonProperty("IBAN")
    private String iban;
    @JsonSerialize(using = RoundingSerializer.class)
    private double balance;
    @JsonIgnore
    private double minBalance;
    private String currency;
    private String type;
    @JsonIgnore
    private double interestRate;
    private List<Card> cards;
    @JsonIgnore
    private List<Transaction> transactions;
    @JsonIgnore
    private List<Discount> discounts = new ArrayList<>(List.of(
            new Discount(0.02, "Food", false, false),
            new Discount(0.05, "Clothes", false, false),
            new Discount(0.1, "Tech", false, false)));

    // Every account needs to keep track of the number of transactions made to each commerciant
    // and the total amount of money spent on each commerciant

    @JsonIgnore
    private Map<String, TransactionInfo> transactionMap = new HashMap<>();

    public Account(final AccountBuilder builder) {
        this.iban = builder.getIban();
        this.balance = builder.getBalance();
        this.minBalance = builder.getMinBalance();
        this.currency = builder.getCurrency();
        this.type = builder.getType();
        this.interestRate = builder.getInterestRate();
        this.cards = builder.getCards();
        this.transactions = builder.getTransactions();
    }

    /**
     * Add a transaction to the account
     * @param commerciant - the commerciant of the transaction
     * @param amount - amount paid
     */
    public double addTransaction(final String commerciant, final double amount) {
        TransactionInfo info = transactionMap.getOrDefault(commerciant, new TransactionInfo());
        info.addTransaction(amount);
        transactionMap.put(commerciant, info);

        // Check if the new transaction qualifies for a new discount
        Commerciant comm = Maps.COMM_MAP.get(commerciant);
        if (comm.getCashbackStrategy().equals("nrOfTransactions")) {
            return switch (info.getCount()) {
                case 2 -> {
                    discounts.get(0).setActive(true);
                    yield -1;
                }
                case 5 -> {
                    discounts.get(1).setActive(true);
                    yield -1;
                }
                case 10 -> {
                    discounts.get(2).setActive(true);
                    yield -1;
                }
                default -> 0;
            };
        } else if (comm.getCashbackStrategy().equals("spendingThreshold")) {
            for (Integer threshold: DiscountUtil.THRESHOLDS) {
                if (info.getTotal() >= threshold) {
                    return getThresholdDiscount(threshold);
                }
            }
        } else {
            // unknown strategy
        }
    }

    private double getThresholdDiscount(final int threshold) {
        ServicePlan plan = Maps.USER_MAP.get(iban).getServicePlan();
        return DiscountUtil.DISCOUNT_MAP.getOrDefault(plan, Collections.emptyMap())
                .getOrDefault(threshold, 0.0);
    }

}
