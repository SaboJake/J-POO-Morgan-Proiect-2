package org.poo.transactions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class Transaction {
    protected int timestamp;
    protected String description;
    @JsonIgnore
    protected String type;

    public Transaction(final int timestamp, final String description) {
        this.timestamp = timestamp;
        this.description = description;
        this.type = "default";
    }
}
