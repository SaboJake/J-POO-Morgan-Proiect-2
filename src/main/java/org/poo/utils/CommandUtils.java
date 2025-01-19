package org.poo.utils;

import org.poo.actors.Account;
import org.poo.actors.Card;
import org.poo.actors.ServicePlan;
import org.poo.actors.User;
import org.poo.banking.CurrencyPair;
import org.poo.banking.ExchangeRate;
import org.poo.exceptions.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class CommandUtils {
    private CommandUtils() {
        throw new IllegalAccessError("Utility class");
    }

    /**
     * Handle exception logic for a user and an account
     * Checks if they exist and if the account is owned by the user
     * @param email - user email
     * @param iban - IBAN of account
     */
    public static void userAccountCheck(final String email, final String iban)
            throws NoAccountException, NoUserException, AccountNotOwnedException {
        User user = Maps.USER_MAP.get(email);
        if (user == null) {
            throw new NoUserException(email);
        }
        Account account = Maps.ACCOUNT_MAP.get(iban);
        if (account == null) {
            throw new NoAccountException(iban);
        }
        if (!account.getType().equals("business") && !user.getAccounts().contains(account)) {
            throw new AccountNotOwnedException(iban + " " + email);
        }
    }

    /**
     * Handle exception logic for a user and a card
     * Check if they exist and if the card is owned by the user
     * @param email - user email
     * @param cardNumber - card number
     */
    public static void userCardCheck(final String email, final String cardNumber)
            throws NoUserException, NoCardException, CardNotOwnedException {
        User user = Maps.USER_MAP.get(email);
        if (user == null) {
            throw new NoUserException(email);
        }
        Card card = Maps.CARD_MAP.get(cardNumber);
        if (card == null) {
            throw new NoCardException(cardNumber);
        }
        Account account = Maps.ACCOUNT_MAP.get(cardNumber);
        if (!account.getType().equals("business") && !user.getAccounts().contains(account)) {
            System.out.println("funny");
            throw new CardNotOwnedException(cardNumber + " " + email);
        }
        if (!Maps.USER_MAP.containsKey(cardNumber)) {
            throw new CardNotOwnedException(cardNumber + " " + email);
        }
    }

    /**
     * Get converted amount form a currency to another
     * @param amount - amount to be converted
     * @param from - form currency
     * @param to - to currency
     * @return - converted amount
     */
    public static double getActualAmount(final double amount, final String from, final String to) {
        if (!from.equals(to)) {
            return amount * ExchangeRate.DIST.get(new CurrencyPair(from, to));
        }
        return amount;
    }

    /**
     * Check if bank transfer is possible and if so return the amount
     * @param from - IBAN or alias of account
     * @param currency - currency needed for conversion
     * @param amount - amount of funds needed to be deducted
     */
    public static double bankTransferCheck(final String from, final String currency,
                                           final double amount)
            throws NoAccountException, InsufficientFundsException {
        Account fromAccount = Maps.ACCOUNT_MAP.get(from);
        if (fromAccount == null) {
            throw new NoAccountException(from);
        }
        double actualAmount = getActualAmount(amount, currency, fromAccount.getCurrency());
        User user = Maps.USER_MAP.get(from);
        double commission = getCommission(actualAmount, user.getServicePlan(),
                fromAccount.getCurrency());
        if (fromAccount.getBalance() < actualAmount + commission) {
            throw new InsufficientFundsException(from);
        }
        return actualAmount + commission;
    }

    /**
     * Rounding function
     * @param value - value to be rounded
     * @param places - number of decimal places
     * @return - rounded value to decimal places
     */
    public static double round(final double value, final int places) {
        return new BigDecimal(value)
                .setScale(places, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * Get the commission for a transaction
     * @param amount - amount of transaction
     * @param servicePlan - service plan of user
     * @param currency - currency of transaction
     * @return - commission amount
     */
    public static double getCommission(final double amount, final ServicePlan servicePlan,
                                       final String currency) {
        final double smallCommission = 0.001, bigCommission = 0.002;
        final int commissionThreshold = 500;
        if (servicePlan == ServicePlan.STANDARD) {
            return amount * bigCommission;
        }
        if (servicePlan == ServicePlan.SILVER
                && ExchangeRate.getRONRate(currency, amount) > commissionThreshold) {
            return amount * smallCommission;
        }
        return 0.0;
    }

    /**
     * Get the cost of upgrading a service plan
     * @param from - current service plan
     * @param to - new service plan
     * @param currency - currency of transaction
     * @return - cost of upgrading
     */
    public static double getUpgradeCost(final ServicePlan from, final ServicePlan to,
                                        final String currency) {
        double amount =  Maps.UPGRADE_MATRIX[from.ordinal()][to.ordinal()];
        return ExchangeRate.getFromRonRate(currency, amount);
    }

    /**
     * Basic checks for a business account
     * @param account - account to check
     * @param email - email of the associated user
     */
    public static void businessCheck(final Account account, final String email) {
        if (account == null) {
            throw new NoAccountException(account.getIban());
        }
        if (!account.getType().equals("business")) {
            throw new NotBusinessAccountException(account.getIban());
        }
        if (!Maps.USER_MAP.containsKey(email)) {
            throw new NoUserException(email);
        }
    }
}
