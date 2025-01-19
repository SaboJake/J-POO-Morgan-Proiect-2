package org.poo.transactions;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class AddInterestTransaction extends Transaction {
    private double amount;
    private String currency;

    public AddInterestTransaction(final int timestamp, final String description,
                                  final double amount, final String currency) {
        super(timestamp, description);
        this.amount = amount;
        this.currency = currency;
        this.type = "addInterest";
    }
}
