package org.poo.actors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.poo.transactions.Transaction;

import java.util.List;

@Setter @Getter @AllArgsConstructor
public class User {
    private String firstName;
    private String lastName;
    private String email;
    private String birthDate;
    private String occupation;
    private List<Account> accounts;
    @JsonIgnore
    private List<Transaction> transactions;
    @JsonIgnore
    private int upgradeProgress;
    @JsonIgnore
    private ServicePlan servicePlan;

    /**
     * Update the user's service plan based on the number of transactions made
     */
    public void updateProgress() {
        upgradeProgress++;
        if (upgradeProgress >= 5 && servicePlan == ServicePlan.SILVER) {
            servicePlan = ServicePlan.GOLD;
        }
    }
}
