package org.poo.banking;

import java.util.*;

public final class ExchangeRate {
    private ExchangeRate() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static final Map<CurrencyPair, Double> DIST = new HashMap<>();
    public static final Set<String> CURRENCIES = new HashSet<>();

    /**
     * Put new Exchange rate
     * @param from - currency FROM which the conversion is made
     * @param to - currency TO which the conversion is made
     * @param rate - exchange rate
     */
    public static void insertExchangeRate(final String from, final String to, final double rate) {
        if (rate == 0) {
            return;
        }
        DIST.put(new CurrencyPair(from, to), rate);
        DIST.put(new CurrencyPair(to, from), 1 / rate);
        DIST.put(new CurrencyPair(from, from), 1.0);
        DIST.put(new CurrencyPair(to, to), 1.0);
        CURRENCIES.add(from);
        CURRENCIES.add(to);
    }

    private static double getDist(final CurrencyPair pair) {
        if (DIST.containsKey(pair)) {
            return DIST.get(pair);
        }
        return Double.POSITIVE_INFINITY;
    }

    /**
     * Roy-Floyd algorithm
     */
    public static void calculateDist() {
        for (String k: CURRENCIES) {
            for (String i: CURRENCIES) {
                for (String j: CURRENCIES) {
                    CurrencyPair ik = new CurrencyPair(i, k);
                    CurrencyPair kj = new CurrencyPair(k, j);
                    CurrencyPair ij = new CurrencyPair(i, j);
                    if (getDist(ik) * getDist(kj) < getDist(ij)) {
                        DIST.put(ij, getDist(ik) * getDist(kj));
                    }
                }
            }
        }
    }
}
