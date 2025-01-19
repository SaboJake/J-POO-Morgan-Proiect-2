package org.poo.utils;

import org.poo.actors.Account;
import org.poo.actors.Discount;
import org.poo.actors.ServicePlan;
import org.poo.actors.User;
import org.poo.banking.ExchangeRate;
import org.poo.transactions.Transaction;

import java.util.List;
import java.util.Map;

public final class DiscountUtil {
    private DiscountUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static final List<Integer> THRESHOLDS = List.of(500, 300, 100);
    public static final Map<ServicePlan, Map<Integer, Double>> DISCOUNT_MAP = Map.ofEntries(
            Map.entry(ServicePlan.STANDARD, Map.of(
                    100, 0.001,
                    300, 0.002,
                    500, 0.0025
            )),
            Map.entry(ServicePlan.STUDENT, Map.of(
                    100, 0.001,
                    300, 0.002,
                    500, 0.0025
            )),
            Map.entry(ServicePlan.SILVER, Map.of(
                    100, 0.003,
                    300, 0.004,
                    500, 0.005
            )),
            Map.entry(ServicePlan.GOLD, Map.of(
                    100, 0.005,
                    300, 0.0055,
                    500, 0.007
            ))
    );

    /**
     * Get the discount based on the service plan and the amount spent
     * @param account - the account of the user
     * @param actualAmount - the amount spent (in account's currency)
     * @param commerciant - the commerciant of the transaction
     */
    public static Transaction discountLogic(final Account account,
                                     final double actualAmount, final String commerciant,
                                     final int timestamp) {
        double ronAmount = ExchangeRate.getRONRate(account.getCurrency(), actualAmount);
        Transaction updatePlan = null;
        final int upgradeThreshold = 300;
        // Update user's service plan
        if (ronAmount >= upgradeThreshold) {
            User user = Maps.USER_MAP.get(account.getIban());
            updatePlan = user.updateProgress(account, timestamp);
        }
        double cashBack = account.addTransaction(commerciant, ronAmount);
        double extra;
        // Check for spendingThreshold discount
        if (cashBack > 0) {
            extra = cashBack * actualAmount;
            account.setBalance(account.getBalance() + extra);
        }
        // Check for discount based on number of transactions
        String commType = Maps.COMM_MAP.get(commerciant).getType();
        for (Discount discount: account.getDiscounts()) {
            // Cashback will be -1 if we just got a discount based on number of transactions
            // This discount shouldn't be applied, as it needs to be applied to the next transaction
            if (discount.getCommerciantType().equals(commType) && discount.isActive()
                    && !discount.isUsed() && cashBack != -1) {
                extra = discount.getAmmount() * actualAmount;
                account.setBalance(account.getBalance() + extra);
                discount.setActive(false);
                discount.setUsed(true);
                break;
            }
        }
        return updatePlan;
    }
}
