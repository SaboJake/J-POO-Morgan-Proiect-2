package org.poo.commands;

import lombok.Getter;
import lombok.Setter;
import org.poo.actors.Account;
import org.poo.actors.Card;
import org.poo.actors.User;
import org.poo.exceptions.*;
import org.poo.fileio.CommandInput;
import org.poo.outputs.SuccessOutput;
import org.poo.utils.CommandUtils;
import org.poo.utils.Maps;

@Setter @Getter
public class DeleteAccount extends BankCommand implements Command {
    private String iban;
    private String email;

    public DeleteAccount(final CommandInput input) {
        this.setCommand(input.getCommand());
        this.setTimestamp(input.getTimestamp());
        if (input.getAlias() != null) {
            this.iban = Maps.ALIAS_MAP.get(input.getAlias());
        } else {
            this.iban = input.getAccount();
        }
        this.email = input.getEmail();
    }

    /**
     * Delete account with given iban of the user with given email
     */
    @Override
    public void execute() throws NoUserException, NoAccountException, AccountNotOwnedException,
            InvalidAccountDelletionException {
        CommandUtils.userAccountCheck(email, iban);
        User user = Maps.USER_MAP.get(email);
        Account account = Maps.ACCOUNT_MAP.get(iban);
        final double threshold = 1e-10;
        if (account.getBalance() > threshold) {
            throw new InvalidAccountDelletionException(iban);
        }
        if (account.getType().equals("business")
                && !account.getBusinessAccount().getOwner().equals(email)) {
            throw new NotAuthorizedException(email);
        }
        if (account.getType().equals("classic")
                && user.getCurrencyAccountMap().get(account.getCurrency()) == account) {
            user.getCurrencyAccountMap().remove(account.getCurrency());
            for (Account account1: user.getAccounts()) {
                if (account1 != account && account1.getType().equals("classic")
                        && account1.getCurrency().equals(account.getCurrency())) {
                    user.getCurrencyAccountMap().put(account.getCurrency(), account1);
                    break;
                }
            }
        }
        user.getAccounts().remove(account);
        for (Card card : account.getCards()) {
            Maps.CARD_MAP.remove(card.getCardNumber());
            Maps.ACCOUNT_MAP.remove(card.getCardNumber());
            Maps.USER_MAP.remove(card.getCardNumber());
        }
        account.getCards().clear();
        this.setOutput(new SuccessOutput("Account deleted", timestamp));
    }
}
