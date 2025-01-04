package org.poo.commands;

import lombok.Getter;
import lombok.Setter;
import org.poo.actors.User;
import org.poo.exceptions.NoUserException;
import org.poo.fileio.CommandInput;
import org.poo.utils.Maps;

@Setter @Getter
public class PrintTransactions extends BankCommand implements Command {
    private String email;

    public PrintTransactions(final CommandInput input) {
        this.setCommand(input.getCommand());
        this.setTimestamp(input.getTimestamp());
        this.email = input.getEmail();
    }
    /**
     * Set output to the transactions of a user
     */
    @Override
    public void execute() throws NoUserException {
        User user = Maps.USER_MAP.get(email);
        if (user == null) {
            throw new NoUserException(email);
        }
        this.output = user.getTransactions();
    }
}
