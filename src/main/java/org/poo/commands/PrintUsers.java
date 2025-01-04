package org.poo.commands;

import lombok.Getter;
import lombok.Setter;
import org.poo.actors.User;
import org.poo.fileio.CommandInput;

import java.util.List;
import java.util.ArrayList;

@Setter @Getter
public class PrintUsers extends BankCommand implements Command {
    private List<User> users;

    public PrintUsers(final CommandInput input) {
        this.setCommand(input.getCommand());
        this.setTimestamp(input.getTimestamp());
        this.users = new ArrayList<>();
    }

    /**
     * Print Users
     */
    @Override
    public void execute() {
        this.setOutput(users);
    }
}
