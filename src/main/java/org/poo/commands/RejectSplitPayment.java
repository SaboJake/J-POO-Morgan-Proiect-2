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

    public RejectSplitPayment(final CommandInput input) {
        this.setCommand(input.getCommand());
        this.setTimestamp(input.getTimestamp());
        this.email = input.getEmail();
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
        user.getSplitPayments().getFirst().setError("One user rejected the payment.");
        user.getSplitPayments().getFirst().initiate();
        user.getSplitPayments().getFirst().remove();
    }
}
