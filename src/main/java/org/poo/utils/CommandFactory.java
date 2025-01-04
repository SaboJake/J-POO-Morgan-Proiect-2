package org.poo.utils;

import org.poo.commands.*;
import org.poo.exceptions.UnknownCommandException;
import org.poo.fileio.CommandInput;

import java.util.Map;
import java.util.function.Function;

public final class CommandFactory {
    private CommandFactory() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    private static final Map<String, Function<CommandInput, BankCommand>> COMMANDS =
            Map.ofEntries(
                    Map.entry("addAccount", AddAccount::new),
                    Map.entry("addFunds", AddFunds::new),
                    Map.entry("addInterest", AddInterest::new),
                    Map.entry("changeInterestRate", ChangeInterestRate::new),
                    Map.entry("checkCardStatus", CheckCardStatus::new),
                    Map.entry("createCard", CreateCard::new),
                    Map.entry("createOneTimeCard", CreateCard::new),
                    Map.entry("deleteAccount", DeleteAccount::new),
                    Map.entry("deleteCard", DeleteCard::new),
                    Map.entry("payOnline", PayOnline::new),
                    Map.entry("printTransactions", PrintTransactions::new),
                    Map.entry("printUsers", PrintUsers::new),
                    Map.entry("sendMoney", SendMoney::new),
                    Map.entry("setAlias", SetAlias::new),
                    Map.entry("setMinimumBalance", SetMinBalance::new),
                    Map.entry("splitPayment", SplitPayment::new),
                    Map.entry("report", Report::new),
                    Map.entry("spendingsReport", SpendingReport::new)
            );

    /**
     * Create a command based on the command name and input
     * @param commandName - name of the command
     * @param input - input for the command
     * @return - the command
     */
    public static BankCommand createCommand(final String commandName, final CommandInput input) {
        Function<CommandInput, BankCommand> constructor = COMMANDS.get(commandName);
        if (constructor != null) {
            return constructor.apply(input);
        }
        throw new UnknownCommandException("Unknown command: " + commandName);
    }

}
