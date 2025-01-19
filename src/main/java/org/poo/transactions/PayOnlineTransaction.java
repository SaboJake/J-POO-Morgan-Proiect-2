package org.poo.transactions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import org.poo.actors.User;
import org.poo.utils.RoundingSerializer;

@Setter @Getter
public class PayOnlineTransaction extends Transaction {
    @JsonSerialize(using = RoundingSerializer.class)
    private double amount;
    private String commerciant;
    @JsonIgnore
    private String email;

    public PayOnlineTransaction(final int timestamp, final String description,
                                final double amount, final String commerciant,
                                final String email) {
        super(timestamp, description);
        this.amount = amount;
        this.commerciant = commerciant;
        this.type = "payOnline";
        this.email = email;
    }
}
