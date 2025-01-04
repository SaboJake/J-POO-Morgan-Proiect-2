package org.poo.transactions;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import org.poo.utils.RoundingSerializer;

import java.util.List;

@Setter @Getter
public class SplitPaymentTransaction extends Transaction {
    private String currency;
    @JsonSerialize(using = RoundingSerializer.class)
    private double amount;
    private List<String> involvedAccounts;

    public SplitPaymentTransaction(final int timestamp, final String description,
                                   final String currency, final double amount,
                                   final List<String> involvedAccounts) {
        super(timestamp, description);
        this.currency = currency;
        this.amount = amount;
        this.involvedAccounts = involvedAccounts;
        this.type = "splitPayment";
    }
}
