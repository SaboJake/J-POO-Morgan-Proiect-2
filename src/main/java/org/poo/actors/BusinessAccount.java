package org.poo.actors;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.poo.banking.AssociateSpending;
import org.poo.banking.ExchangeRate;
import org.poo.transactions.AddFundsTransaction;
import org.poo.utils.Maps;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Setter @Getter @NoArgsConstructor
public class BusinessAccount {
    private Account account;
    private String owner;

    private double spendLimit;
    private double depositLimit;

    private List<AddFundsTransaction> addFundsTransactions;

    private class Assosciate {
        private String type;
        private Map<String, Card> cards;

        Assosciate(final String type) {
            this.type = type;
            this.cards = new HashMap<>();
        }
    }

    private Map<String, Assosciate> assosciateMap;
    private List<String> associates;

    public BusinessAccount(final String owner, final Account account) {
        final int initialLimit = 500;
        this.owner = owner;
        this.account = account;
        this.assosciateMap = new HashMap<>();
        this.spendLimit = ExchangeRate.getFromRonRate(account.getCurrency(), initialLimit);
        this.depositLimit = spendLimit;
        this.addFundsTransactions = new ArrayList<>();
        this.associates = new ArrayList<>();
    }

    /**
     * Check if the given email is a manager
     * @param email - the email to check
     * @return - true if the email is a manager
     */
    public boolean isManager(final String email) {
        Assosciate assosciate = assosciateMap.get(email);
        return assosciate != null && assosciate.type.equals("manager");
    }

    /**
     * Check if the given email is an employee
     * @param email - the email to check
     * @return - true if the email is an employee
     */
    public boolean isEmployee(final String email) {
        Assosciate assosciate = assosciateMap.get(email);
        return assosciate != null && assosciate.type.equals("employee");
    }

    /**
     * Add an employee to the business account
     * @param email - the email of the employee
     */
    public void addAssociate(final String email, final String type) {
        assosciateMap.put(email, new Assosciate(type));
        associates.add(email);
    }

    /**
     * Check if the given email is an associate
     * @param email - the email to check
     * @return - true if the email is an associate
     */
    public boolean isAssociate(final String email) {
        return assosciateMap.containsKey(email) || email.equals(owner);
    }

    /**
     * Check if the given email can spend the given amount
     * @param email - the email to check
     * @param amount - the amount to spend
     * @return - true if the email can spend the amount
     */
    public boolean canSpend(final String email, final double amount) {
        if (email.equals(owner)) {
            return true;
        }
        Assosciate assosciate = assosciateMap.get(email);
        return assosciate != null && (isManager(email) || spendLimit >= amount);
    }

    /**
     * Check if the given email can deposit the given amount
     * @param email - the email to check
     * @param amount - the amount to deposit
     * @return - true if the email can deposit the amount
     */
    public boolean canDeposit(final String email, final double amount) {
        if (email.equals(owner)) {
            return true;
        }
        Assosciate assosciate = assosciateMap.get(email);
        return assosciate != null && (isManager(email) || depositLimit >= amount);
    }

    /**
     * Add a card to the given email
     * @param email - the email to add the card
     * @param card - the card to add
     */
    public void addCard(final String email, final Card card) {
        Assosciate assosciate = assosciateMap.get(email);
        if (assosciate != null) {
            assosciate.cards.put(card.getCardNumber(), card);
        }
    }

    /**
     * Check if the given email has the given card
     * @param email - the email to check
     * @param card - the card to check
     * @return - true if the email has the card
     */
    public boolean hasCard(final String email, final Card card) {
        Assosciate assosciate = assosciateMap.get(email);
        return assosciate != null && assosciate.cards.containsKey(card.getCardNumber());
    }

    /**
     * Remove the given card from the given email
     * @param card - the card to remove
     */
    public void removeCard(final Card card) {
        for (Assosciate assosciate: assosciateMap.values()) {
            assosciate.cards.remove(card.getCardNumber());
        }
    }

    /**
     * Update map with associates not already in the map
     * @param map - the map to update
     */
    public void updateMap(final Map<String, Double> map) {
        assosciateMap.keySet().stream()
                .filter(email -> !map.containsKey(email))
                .forEach(email -> map.put(email, 0.0));
    }

    private List<AssociateSpending> getAssociateSpending(
            final Map<String, Double> spendingMap,
            final Map<String, Double> depositMap,
            final Predicate<String> predicate) {
        return spendingMap.entrySet().stream()
                .filter(entry -> predicate.test(entry.getKey()))
                .map(entry -> {
                    User user = Maps.USER_MAP.get(entry.getKey());
                    String name = user.getLastName() + " " + user.getFirstName();
                    double spent = entry.getValue();
                    double deposited = depositMap.getOrDefault(entry.getKey(), 0.0);
                    return new AssociateSpending(name, spent, deposited);
                })
//                .sorted(Comparator.comparing(associate -> {
//                    String[] names = associate.getUsername().split(" ");
//                    return names.length > 1 ? names[1] : names[0];
//                }))
                .collect(Collectors.toList());
    }

    /**
     * Get the spending of the employees
     * @param spendingMap - the spending map
     * @param depositMap - the deposit map
     * @return - the list of associate spending
     */
    public List<AssociateSpending> getEmployeesSpending(
            final Map<String, Double> spendingMap,
            final Map<String, Double> depositMap) {
        return getAssociateSpending(spendingMap, depositMap, this::isEmployee);
    }

    /**
     * Get the spending of the managers
     * @param spendingMap - the spending map
     * @param depositMap - the deposit map
     * @return - the list of associate spending
     */
    public List<AssociateSpending> getManagersSpending(
            final Map<String, Double> spendingMap,
            final Map<String, Double> depositMap) {
        return getAssociateSpending(spendingMap, depositMap, this::isManager);
    }
}
