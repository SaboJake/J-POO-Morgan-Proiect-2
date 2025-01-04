package org.poo.actors;

import lombok.Getter;
import lombok.Setter;
import org.poo.transactions.Transaction;


import java.util.List;
import java.util.ArrayList;

@Setter @Getter
public class AccountBuilder {
    private String iban;
    private double balance;
    private double minBalance;
    private String currency;
    private String type;
    private double interestRate;
    private List<Card> cards;
    private List<Transaction> transactions;

    public AccountBuilder(final String iban, final double balance, final String currency,
                          final String accountType) {
        this.iban = iban;
        this.balance = balance;
        this.currency = currency;
        this.type = accountType;
        this.cards = new ArrayList<>();
        this.transactions = new ArrayList<>();
    }

    /**
     * Builder method
     * @param functionMinBalance - what could this be?????
     * @return - builder with minBalance field set
     */
    public AccountBuilder withMinBalance(final double functionMinBalance) {
        this.minBalance = functionMinBalance;
        return this;
    }

    /**
     * Build it with interest rate
     * @param functionInterestRate - the interest rate to be set
     * @return - builder with interestRate fields ser
     */
    public AccountBuilder withInterestRate(final double functionInterestRate) {
        this.interestRate = functionInterestRate;
        return this;
    }

    /**
     * Return new account from the builder
     */
    public Account build() {
        return new Account(this);
    }
}
