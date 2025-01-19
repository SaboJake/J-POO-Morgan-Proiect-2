package org.poo.commands;

import lombok.Getter;
import lombok.Setter;
import org.poo.actors.User;
import org.poo.exceptions.NoUserException;
import org.poo.fileio.CommandInput;
import org.poo.utils.Maps;

@Setter @Getter
public class AcceptSplitPayment extends BankCommand implements Command {
    private String email;
    private String splitPaymentType;

    public AcceptSplitPayment(final CommandInput input) {
        this.setCommand(input.getCommand());
        this.setTimestamp(input.getTimestamp());
        this.email = input.getEmail();
        this.splitPaymentType = input.getSplitPaymentType();
    }

    /**
     * Accepts a split payment
     */
    @Override
    public void execute() {
        User user = Maps.USER_MAP.get(email);
        if (user == null || !email.contains("@")) {
            throw new NoUserException(email);
        }
        // System.out.println("User " + user.getEmail() + " accepted the split payment.");
        // Accept next split payment that isn't already accepted
        NewSplitPayment splitPayment = null;
        for (int i = 0; i < user.getSplitPayments().size(); i++) {
            splitPayment = user.getSplitPayments().get(i);
            if (splitPayment.getType().equals(splitPaymentType)
                && !splitPayment.getUsers().contains(user.getEmail())) {
                splitPayment.getUsers().add(user.getEmail());
                break;
            }
        }
        if (splitPayment == null) {
            return;
        }
        if (splitPayment.isAccepted()) {
            splitPayment.initiate();
            splitPayment.remove();
        }
    }
}
