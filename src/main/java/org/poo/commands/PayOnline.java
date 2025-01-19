package org.poo.commands;

import lombok.Getter;
import lombok.Setter;
import org.poo.actors.Card;
import org.poo.actors.cardState.CardStateFactory;
import org.poo.exceptions.CardNotOwnedException;
import org.poo.exceptions.InsufficientFundsException;
import org.poo.exceptions.NoCardException;
import org.poo.exceptions.NoUserException;
import org.poo.fileio.CommandInput;
import org.poo.utils.CommandUtils;
import org.poo.utils.Maps;

@Setter @Getter
public class PayOnline extends BankCommand implements Command {
    private String cardNumber;
    private double amount;
    private String currency;
    private String description;
    private String commerciant;
    private String email;

    private static final int WARING_THRESHOLD = 30;

    public PayOnline(final CommandInput input) {
        this.setCommand(input.getCommand());
        this.setTimestamp(input.getTimestamp());
        this.cardNumber = input.getCardNumber();
        this.amount = input.getAmount();
        this.currency = input.getCurrency();
        this.description = input.getDescription();
        this.commerciant = input.getCommerciant();
        this.email = input.getEmail();
    }

    /**
     * Pay by card deducting funds from the user's account
     */
    @Override
    public void execute() throws InsufficientFundsException,
            NoUserException, NoCardException, CardNotOwnedException {
        CommandUtils.userCardCheck(email, cardNumber);
        Card card = Maps.CARD_MAP.get(cardNumber);
        card.setState(CardStateFactory.createCardState(card.getStatus(),
                timestamp, amount, currency, commerciant, email));
        card.handle();
    }
}
