package org.poo.actors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.poo.transactions.Transaction;
import org.poo.utils.RoundingSerializer;


import java.util.List;

@Setter @Getter @AllArgsConstructor
public class Account {
    @JsonProperty("IBAN")
    private String iban;
    @JsonSerialize(using = RoundingSerializer.class)
    private double balance;
    @JsonIgnore
    private double minBalance;
    private String currency;

    private String type;
    @JsonIgnore
    private double interestRate;
    private List<Card> cards;
    @JsonIgnore
    private List<Transaction> transactions;

    public Account(final AccountBuilder builder) {
        this.iban = builder.getIban();
        this.balance = builder.getBalance();
        this.minBalance = builder.getMinBalance();
        this.currency = builder.getCurrency();
        this.type = builder.getType();
        this.interestRate = builder.getInterestRate();
        this.cards = builder.getCards();
        this.transactions = builder.getTransactions();
    }
}
