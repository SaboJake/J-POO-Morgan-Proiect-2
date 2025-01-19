package org.poo.commands;

import lombok.Getter;
import lombok.Setter;
import org.poo.actors.Account;
import org.poo.actors.Card;
import org.poo.actors.User;
import org.poo.actors.cardState.RegularState;
import org.poo.exceptions.AccountNotOwnedException;
import org.poo.exceptions.NoAccountException;
import org.poo.exceptions.NoUserException;
import org.poo.fileio.CommandInput;
import org.poo.transactions.CardTransaction;
import org.poo.utils.CommandUtils;
import org.poo.utils.Maps;
import org.poo.utils.Utils;

@Setter @Getter
public class CreateCard extends BankCommand implements Command {
    private String iban;
    private String email;
    private boolean oneTime;

    public CreateCard(final CommandInput input) {
        this.setCommand(input.getCommand());
        this.setTimestamp(input.getTimestamp());
        this.oneTime = input.getCommand().equals("createOneTimeCard");
        if (input.getAlias() != null) {
            this.iban = Maps.ALIAS_MAP.get(input.getAlias());
        } else {
            this.iban = input.getAccount();
        }
        this.email = input.getEmail();
    }

    /**
     * Create new card for an account of the user
     */
    @Override
    public void execute() throws NoUserException, NoAccountException, AccountNotOwnedException {
        CommandUtils.userAccountCheck(email, iban);
        Card card = new Card(Utils.generateCardNumber(), "active",
                new RegularState(), oneTime);
        Account account = Maps.ACCOUNT_MAP.get(iban);
        account.getCards().add(card);
        User user = Maps.USER_MAP.get(email);
        // card number -> account
        Maps.ACCOUNT_MAP.put(card.getCardNumber(), account);
        // card number -> card
        Maps.CARD_MAP.put(card.getCardNumber(), card);
        // card number -> user
        Maps.USER_MAP.put(card.getCardNumber(), user);
        CardTransaction tr = new CardTransaction(timestamp, "New card created",
                card.getCardNumber(), email, iban);
        user.getTransactions().add(tr);
        account.getTransactions().add(tr);
        if (account.getType().equals("business")) {
            account.getBusinessAccount().addCard(email, card);
        }
    }
}
