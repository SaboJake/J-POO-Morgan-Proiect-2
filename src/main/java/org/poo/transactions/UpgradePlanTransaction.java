package org.poo.transactions;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class UpgradePlanTransaction extends Transaction {
    private String accountIBAN;
    private String newPlanType;

    public UpgradePlanTransaction(final int timestamp, final String description,
                                    final String accountIBAN, final String newPlanType) {
        super(timestamp, description);
        this.accountIBAN = accountIBAN;
        this.newPlanType = newPlanType;
        this.type = "upgradePlan";
    }
}
