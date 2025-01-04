package org.poo.transactions;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import org.poo.utils.RoundingSerializer;

@Setter @Getter
public class PayOnlineTransaction extends Transaction {
    @JsonSerialize(using = RoundingSerializer.class)
    private double amount;
    private String commerciant;

    public PayOnlineTransaction(final int timestamp, final String description,
                                final double amount, final String commerciant) {
        super(timestamp, description);
        this.amount = amount;
        this.commerciant = commerciant;
        this.type = "payOnline";
    }
}
