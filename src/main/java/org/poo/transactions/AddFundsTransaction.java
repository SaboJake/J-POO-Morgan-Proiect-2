package org.poo.transactions;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class AddFundsTransaction extends Transaction {
    private double amount;
    private String email;

    public AddFundsTransaction(final int timestamp, final String description, final double amount,
                               final String email) {
        super(timestamp, description);
        this.amount = amount;
        this.type = "addFunds";
        this.email = email;
    }
}
