package org.poo.commands;

import lombok.Getter;
import lombok.Setter;
import org.poo.actors.User;
import org.poo.exceptions.NoUserException;
import org.poo.fileio.CommandInput;
import org.poo.utils.Maps;

@Setter @Getter
public class RejectSplitPayment extends BankCommand implements Command {
    private String email;
    private String splitPaymentType;

    public RejectSplitPayment(final CommandInput input) {
        this.setCommand(input.getCommand());
        this.setTimestamp(input.getTimestamp());
        this.email = input.getEmail();
        this.splitPaymentType = input.getSplitPaymentType();
    }

    /**
     * Rejects a split payment
     */
    @Override
    public void execute() {
        System.out.println("Reject " + timestamp + " " + email);
        User user = Maps.USER_MAP.get(email);
        if (user == null || !email.contains("@")) {
            throw new NoUserException(email);
        }
        NewSplitPayment splitPayment = null;
        for (int i = 0; i < user.getSplitPayments().size(); i++) {
            if (user.getSplitPayments().get(i).getType().equals(splitPaymentType)) {
                splitPayment = user.getSplitPayments().get(i);
                break;
            }
        }
        if (splitPayment == null) {
            return;
        }
        splitPayment.setError("One user rejected the payment.");
        splitPayment.initiate();
        splitPayment.remove();
    }
}
