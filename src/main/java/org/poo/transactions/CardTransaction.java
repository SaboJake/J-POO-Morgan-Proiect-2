package org.poo.transactions;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class CardTransaction extends Transaction {
    private String card;
    private String cardHolder;
    private String account;

    public CardTransaction(final int timestamp, final String description,
                           final String card, final String cardHolder, final String account) {
        super(timestamp, description);
        this.card = card;
        this.cardHolder = cardHolder;
        this.account = account;
        this.type = "createCard";
    }
}
