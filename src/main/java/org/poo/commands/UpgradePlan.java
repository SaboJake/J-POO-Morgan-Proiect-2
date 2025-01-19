package org.poo.commands;

import lombok.Getter;
import lombok.Setter;
import org.poo.actors.Account;
import org.poo.actors.ServicePlan;
import org.poo.actors.User;
import org.poo.exceptions.InsufficientFundsException;
import org.poo.exceptions.InvalidServicePlanException;
import org.poo.exceptions.NoAccountException;
import org.poo.fileio.CommandInput;
import org.poo.transactions.Transaction;
import org.poo.transactions.UpgradePlanTransaction;
import org.poo.utils.CommandUtils;
import org.poo.utils.Maps;

@Setter @Getter
public class UpgradePlan extends BankCommand implements Command {
    private String newPlan;
    private String iban;

    public UpgradePlan(final CommandInput input) {
        this.setCommand(input.getCommand());
        this.setTimestamp(input.getTimestamp());
        this.newPlan = input.getNewPlanType();
        this.iban = input.getAccount();
    }

    /**
     * Upgrades the service plan of a user
     */
    public void execute() {
        Account account = Maps.ACCOUNT_MAP.get(iban);
        // Account not found
        if (account == null) {
            throw new NoAccountException(iban);
        }
        User user = Maps.USER_MAP.get(iban);
        ServicePlan plan = Maps.PLAN_MAP.get(newPlan);
        if (user.getServicePlan() == plan) {
            Transaction tr = new Transaction(getTimestamp(), "The user already has the "
                    + newPlan + " plan.");
            user.getTransactions().add(tr);
            account.getTransactions().add(tr);
            throw new InvalidServicePlanException("The user already has the " + newPlan + " plan.");
        }
        if (user.getServicePlan().ordinal() > plan.ordinal()) {
            Transaction tr = new Transaction(getTimestamp(), "You cannot downgrade your plan.");
            user.getTransactions().add(tr);
            account.getTransactions().add(tr);
            throw new InvalidServicePlanException("You cannot downgrade your plan.");
        }
        double amount = CommandUtils.getUpgradeCost(user.getServicePlan(), plan,
                account.getCurrency());
        if (account.getBalance() < amount) {
            throw new InsufficientFundsException(iban);
        }
        account.setBalance(account.getBalance() - amount);
        user.setServicePlan(plan);

        UpgradePlanTransaction transaction = new UpgradePlanTransaction(getTimestamp(),
                "Upgrade plan", iban, newPlan);
        user.getTransactions().add(transaction);
        account.getTransactions().add(transaction);
    }
}
