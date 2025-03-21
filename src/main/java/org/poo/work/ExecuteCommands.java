package org.poo.work;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.actors.Account;
import org.poo.actors.User;
import org.poo.banking.Bank;
import org.poo.commands.BankCommand;
import org.poo.commands.PrintUsers;
import org.poo.exceptions.*;
import org.poo.fileio.CommandInput;
import org.poo.fileio.ObjectInput;
import org.poo.outputs.DefaultOutput;
import org.poo.outputs.ErrorOutput;
import org.poo.transactions.Transaction;
import org.poo.utils.CommandFactory;
import org.poo.utils.Maps;

public final class ExecuteCommands {
    private ExecuteCommands() {
        throw new UnsupportedOperationException("This class cannot be instantiated.");
    }

    private static void addCommand(final BankCommand command, final Object output,
                                   final ArrayNode out, final ObjectMapper mapper) {
        BankCommand outputCommand = new BankCommand(command.getCommand(), output,
                command.getTimestamp());
        ObjectNode node = mapper.valueToTree(outputCommand);
        out.add(node);
    }

    private static void addDefaultOutput(final BankCommand command, final String message,
                                         final ArrayNode out, final ObjectMapper mapper) {
        addCommand(command, new DefaultOutput(command.getTimestamp(), message), out, mapper);
    }

    /**
     * Execute all commands and send output
     * @param bank - used to get all users for print users
     * @param in - input
     * @param out - output
     */
    public static void execute(final Bank bank, final ObjectInput in, final ArrayNode out) {
        ObjectMapper mapper = new ObjectMapper();

        for (CommandInput commandInput: in.getCommands()) {
            BankCommand command;
            try {
                command = CommandFactory.createCommand(commandInput.getCommand(), commandInput);
            } catch (UnknownCommandException e) {
                continue;
            }
            try {
                if (commandInput.getCommand().equals("printUsers")) {
                    ((PrintUsers) command).setUsers(bank.getUsers());
                }
                command.execute();
                if (command.getOutput() != null) {
                    addCommand(command, command.getOutput(), out, mapper);
                }
            } catch (NoCardException e) {
                if (!command.getCommand().equals("deleteCard")) {
                    addDefaultOutput(command, "Card not found", out, mapper);
                }
            } catch (NotSavingsAccountException e) {
                addDefaultOutput(command, "This is not a savings account", out, mapper);
            } catch (InvalidAccountDelletionException e) {
                String err = "Account couldn't be deleted - see org.poo.transactions for details";
                addCommand(command, new ErrorOutput(err, command.getTimestamp()), out, mapper);
                Transaction tr = new Transaction(command.getTimestamp(),
                        "Account couldn't be deleted - there are funds remaining");
                User user = Maps.USER_MAP.get(e.getMessage());
                Account account = Maps.ACCOUNT_MAP.get(e.getMessage());
                user.getTransactions().add(tr);
                account.getTransactions().add(tr);
            } catch (NoAccountException e) {
                if (command.getCommand().equals("upgradePlan")) {
                    addDefaultOutput(command, "Account not found", out, mapper);
                } else {
                    addDefaultOutput(command, "User not found", out, mapper);
                }
            } catch (NoClassicAccountException e) {
                Transaction tr = new Transaction(command.getTimestamp(),
                        "You do not have a classic account.");
                Account account = Maps.ACCOUNT_MAP.get(e.getMessage());
                User user = Maps.USER_MAP.get(e.getMessage());
                user.getTransactions().add(tr);
                account.getTransactions().add(tr);
            } catch (NotAuthorizedException e) {
                if (e.getMessage().equals("You must be owner in order to change spending limit.")
                || e.getMessage().equals("You must be owner in order to change deposit limit.")) {
                    addDefaultOutput(command, e.getMessage(), out, mapper);
                } else if (e.getMessage().equals("card not associated with user")) {
                    addDefaultOutput(command, "Card not found", out, mapper);
                }
            } catch (NoUserException e) {
                addDefaultOutput(command, "User not found", out, mapper);
            } catch (NotBusinessAccountException e) {
                addDefaultOutput(command, "This is not a business account", out, mapper);
            } catch (CardNotOwnedException e) {
                addDefaultOutput(command, "Card not found", out, mapper);
            } catch (NoOutputNecessaryException e) {
                e.handle(command);
            } catch (Exception e) {
                // Handle unknown error
            }
        }
    }
}
