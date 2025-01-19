package org.poo.actors.cardState;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.poo.actors.Account;
import org.poo.actors.Card;
import org.poo.actors.User;
import org.poo.banking.CurrencyPair;
import org.poo.banking.ExchangeRate;
import org.poo.exceptions.InsufficientFundsException;
import org.poo.exceptions.NotAuthorizedException;
import org.poo.transactions.CardTransaction;
import org.poo.transactions.PayOnlineTransaction;
import org.poo.transactions.Transaction;
import org.poo.utils.CommandUtils;
import org.poo.utils.DiscountUtil;
import org.poo.utils.Maps;
import org.poo.utils.Utils;

@AllArgsConstructor @NoArgsConstructor
public class RegularState implements CardState {
    private int timestamp;
    private double amount;
    private String currency;
    private String commerciant;
    private String email;

    private static final int WARING_THRESHOLD = 30;
    /**
     * Handles the card
     * @param card - the card to be handled
     */
    @Override
    public void handle(final Card card) {
        if (amount <= 0) {
            return;
        }
        User user = Maps.USER_MAP.get(email);
        Account account = Maps.ACCOUNT_MAP.get(card.getCardNumber());

        double actualAmount = amount;
        if (!currency.equals(account.getCurrency())) {
            double r = ExchangeRate.DIST.get(new CurrencyPair(currency, account.getCurrency()));
            actualAmount *= r;
        }
        double commission = CommandUtils.getCommission(actualAmount,
                user.getServicePlan(), account.getCurrency());

        if (account.getType().equals("business")) {
            commission = CommandUtils.getCommission(actualAmount,
                    Maps.USER_MAP.get(account.getIban()).getServicePlan(), account.getCurrency());
            if (!account.getBusinessAccount().getAssosciateMap().containsKey(user.getEmail())
                    && !account.getBusinessAccount().getOwner().equals(user.getEmail())) {
                throw new NotAuthorizedException("card not associated with user");
            }
            if (!account.getBusinessAccount().canSpend(user.getEmail(),
                    actualAmount + commission)) {
                throw new NotAuthorizedException(user.getEmail());
            }
        }

        if (account.getBalance() < actualAmount + commission) {
            throw new InsufficientFundsException(account.getIban());
        }

        if (account.getMinBalance() > 0
                && account.getBalance() - actualAmount - commission
                + WARING_THRESHOLD < account.getMinBalance()) {
            card.setState(new FrozenState(timestamp,
                    "You have reached the minimum amount of funds, the card will be frozen"));
            card.handle();
            return;
        }
        account.setBalance(account.getBalance() - actualAmount - commission);
        // Check for discounts (also upgrade service plan if needed)
        Transaction updatePlan = DiscountUtil
                .discountLogic(account, actualAmount, commerciant, timestamp);

        // Add transaction
        PayOnlineTransaction tr1 = new PayOnlineTransaction(timestamp, "Card payment",
                actualAmount, commerciant, user.getEmail());
        user.getTransactions().add(tr1);
        account.getTransactions().add(tr1);

        String cardNumber = card.getCardNumber();
        // Replace one time use card
        if (card.isOneTime()) {
            Card newCard = new Card(Utils.generateCardNumber(), "active",
                    new RegularState(), true);
            int index = account.getCards().indexOf(card);
            account.getCards().set(index, newCard);

            Maps.CARD_MAP.remove(cardNumber);
            Maps.ACCOUNT_MAP.remove(cardNumber);
            Maps.USER_MAP.remove(cardNumber);
            CardTransaction tr2 = new CardTransaction(timestamp, "The card has been destroyed",
                    cardNumber, user.getEmail(), account.getIban());
            user.getTransactions().add(tr2);
            account.getTransactions().add(tr2);

            Maps.CARD_MAP.put(newCard.getCardNumber(), newCard);
            Maps.ACCOUNT_MAP.put(newCard.getCardNumber(), account);
            Maps.USER_MAP.put(newCard.getCardNumber(), user);
            CardTransaction tr3 = new CardTransaction(timestamp, "New card created",
                    newCard.getCardNumber(), user.getEmail(), account.getIban());
            user.getTransactions().add(tr3);
            account.getTransactions().add(tr3);
        }
        if (updatePlan != null) {
            user.getTransactions().add(updatePlan);
            account.getTransactions().add(updatePlan);
        }
    }
}
