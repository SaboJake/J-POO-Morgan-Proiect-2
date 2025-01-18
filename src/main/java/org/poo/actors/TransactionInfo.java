package org.poo.actors;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class TransactionInfo {
    private int count;
    private double total;

    public TransactionInfo() {
        this.count = 0;
        this.total = 0.0;
    }

    /**
     * Add a transaction to the total amount and increase the count
     * @param amount - the amount of the transaction
     */
    public void addTransaction(final double amount) {
        this.count++;
        this.total += amount;
    }
}
