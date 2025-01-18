package org.poo.work;

import org.poo.actors.Commerciant;
import org.poo.actors.ServicePlan;
import org.poo.actors.User;
import org.poo.banking.ExchangeRate;
import org.poo.fileio.CommerciantInput;
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
            ServicePlan plan = userInput.getOccupation().equals("Student")
                    ? ServicePlan.STUDENT : ServicePlan.STANDARD;
            users.add(new User(userInput.getFirstName(), userInput.getLastName(),
                    userInput.getEmail(), userInput.getBirthDate(), userInput.getOccupation(),
                    new ArrayList<>(), new ArrayList<>(), 0, plan));
            Maps.USER_MAP.put(userInput.getEmail(), users.getLast());
        }
        return users;
    }

    /**
     * Get array list of commerciants from the input
     * @param input - input
     * @return - array list of commerciants
     */
    public static List<Commerciant> initCommerciants(final ObjectInput input) {
        List<Commerciant> commerciants = new ArrayList<>();
        for (CommerciantInput commerciantInput : input.getCommerciants()) {
            commerciants.add(new Commerciant(commerciantInput.getCommerciant(),
                    commerciantInput.getId(), commerciantInput.getAccount(),
                    commerciantInput.getType(), commerciantInput.getCashbackStrategy()));
            Maps.COMM_MAP.put(commerciantInput.getCommerciant(), commerciants.getLast());
            Maps.COMM_ACCOUNT_MAP.put(commerciantInput.getAccount(),
                    commerciants.getLast());
        }
        return commerciants;
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
