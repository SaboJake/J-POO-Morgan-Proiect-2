package org.poo.transactions;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class WithdrawSavingsTransaction extends Transaction {
    private String classicAccountIBAN;
    private String savingsAccountIBAN;
    private double amount;

    public WithdrawSavingsTransaction(final int timestamp, final String description,
                                      final String classicAccountIBAN, final String savingsAccountIBAN,
                                      final double amount) {
        super(timestamp, description);
        this.classicAccountIBAN = classicAccountIBAN;
        this.savingsAccountIBAN = savingsAccountIBAN;
        this.type = "withdrawSavings";
        this.amount = amount;
    }
}
