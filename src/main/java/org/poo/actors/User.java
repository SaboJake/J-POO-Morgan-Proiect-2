package org.poo.actors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.poo.commands.NewSplitPayment;
import org.poo.transactions.Transaction;

import java.util.*;

@Setter @Getter @AllArgsConstructor
public class User {
    private String firstName;
    private String lastName;
    private String email;
    @JsonIgnore
    private String birthDate;
    @JsonIgnore
    private String occupation;
    private List<Account> accounts;
    @JsonIgnore
    private List<Transaction> transactions;
    @JsonIgnore
    private int upgradeProgress;
    @JsonIgnore
    private ServicePlan servicePlan;
    // First account created with "key" as the currency
    @JsonIgnore
    private Map<String, Account> currencyAccountMap;
    @JsonIgnore
    private List<NewSplitPayment> splitPayments;

    public User(final String firstName, final String lastName, final String email,
                final String birthDate, final String occupation) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.birthDate = birthDate;
        this.occupation = occupation;
        this.accounts = new ArrayList<>();
        this.transactions = new ArrayList<>();
        this.upgradeProgress = 0;
        if (occupation.equals("student")) {
            this.servicePlan = ServicePlan.STUDENT;
        } else {
            this.servicePlan = ServicePlan.STANDARD;
        }
        this.currencyAccountMap = new HashMap<>();
        this.splitPayments = new ArrayList<>();
    }

    /**
     * Update the user's service plan based on the number of transactions made
     */
    public void updateProgress() {
        upgradeProgress++;
        if (upgradeProgress >= 5 && servicePlan == ServicePlan.SILVER) {
            servicePlan = ServicePlan.GOLD;
        }
    }

    /**
     * Get the user's age
     * @return age
     */
    @JsonIgnore
    public int getAge() {
        String date = birthDate.split("-")[0];
        return 2024 - Integer.parseInt(date);
    }
}
