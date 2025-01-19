package org.poo.commands;

import lombok.Getter;
import lombok.Setter;
import org.poo.actors.Account;
import org.poo.actors.Card;
import org.poo.actors.User;
import org.poo.exceptions.CardNotOwnedException;
import org.poo.exceptions.InsufficientFundsException;
import org.poo.exceptions.NoCardException;
import org.poo.exceptions.NoUserException;
import org.poo.fileio.CommandInput;
import org.poo.transactions.CashWithdrawalTransaction;
import org.poo.utils.CommandUtils;
import org.poo.utils.Maps;
import org.poo.banking.ExchangeRate;

@Setter @Getter
public class CashWithdrawal extends BankCommand implements Command {
    private String cardNumber;
    private double amount;
    private String email;
    private String location;

    public CashWithdrawal(final CommandInput input) {
        this.setCommand(input.getCommand());
        this.setTimestamp(input.getTimestamp());
        this.cardNumber = input.getCardNumber();
        this.amount = input.getAmount();
        this.email = input.getEmail();
        this.location = input.getLocation();
    }

    /**
     * Withdraws money from an ATM
     */
    public void execute() {
        Card card = Maps.CARD_MAP.get(cardNumber);
        if (card == null) {
            throw new NoCardException(cardNumber);
        }
        User user = Maps.USER_MAP.get(email);
        if (user == null) {
            throw new NoUserException(email);
        }
        if (!Maps.USER_MAP.get(cardNumber).getEmail().equals(email)) {
            throw new CardNotOwnedException(cardNumber + " " + email);
        }
        Account account = Maps.ACCOUNT_MAP.get(cardNumber);
        if (account.getType().equals("business")) {
            System.out.println("Business accounts cash withdrawal");
        }
        double actualAmount = ExchangeRate.getFromRonRate(account.getCurrency(), amount);
        double commission = CommandUtils.getCommission(actualAmount,
                user.getServicePlan(), account.getCurrency());
        // commission = 0;
        if (account.getBalance() < actualAmount + commission) {
            throw new InsufficientFundsException(account.getIban());
        }
        account.setBalance(account.getBalance() - actualAmount - commission);
        // Add transaction
        CashWithdrawalTransaction transaction = new CashWithdrawalTransaction(
                getTimestamp(), "Cash withdrawal of " + amount, amount);
        account.getTransactions().add(transaction);
        user.getTransactions().add(transaction);
    }
}
