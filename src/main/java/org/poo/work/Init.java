package org.poo.work;

import org.poo.actors.User;
import org.poo.banking.ExchangeRate;
import org.poo.fileio.ExchangeInput;
import org.poo.fileio.ObjectInput;
import org.poo.fileio.UserInput;
import org.poo.utils.Maps;

import java.util.List;
import java.util.ArrayList;

public final class Init {
    private Init() {
        throw new UnsupportedOperationException("This class cannot be instantiated.");
    }

    /**
     * Get array list of users from the input
     * @param input - input
     * @return - array list of users
     */
    public static List<User> initUsers(final ObjectInput input) {
        List<User> users = new ArrayList<>();
        for (UserInput userInput: input.getUsers()) {
            users.add(new User(userInput.getFirstName(), userInput.getLastName(),
                    userInput.getEmail(), new ArrayList<>(), new ArrayList<>()));
            Maps.USER_MAP.put(userInput.getEmail(), users.getLast());
        }
        return users;
    }

    /**
     * Set all exchange rates
     * @param input - input
     */
    public static void initExchange(final ObjectInput input) {
        for (ExchangeInput exchangeInput: input.getExchangeRates()) {
            ExchangeRate.insertExchangeRate(exchangeInput.getFrom(),
                    exchangeInput.getTo(), exchangeInput.getRate());
        }
        ExchangeRate.calculateDist();
    }
}
