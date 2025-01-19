package org.poo.transactions;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class CashWithdrawalTransaction extends Transaction {
    private double amount;

    public CashWithdrawalTransaction(final int timestamp, final String description,
                                     final double amount) {
        super(timestamp, description);
        this.amount = amount;
        this.type = "cashWithdrawal";
    }
}
