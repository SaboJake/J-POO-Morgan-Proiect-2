package org.poo.transactions;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class SplitPaymentErrorTransaction extends SplitPaymentTransaction {
    private String error;

    public SplitPaymentErrorTransaction(final SplitPaymentTransaction tr, final String error) {
        super(tr.getTimestamp(), tr.getDescription(), tr.getCurrency(), tr.getAmount(),
                tr.getInvolvedAccounts());
        this.error = error;
        this.type = "splitPaymentError";
    }
}
