package org.poo.work;

import org.poo.banking.ExchangeRate;
import org.poo.utils.Maps;

public final class Cleanup {
    private Cleanup() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Delete all contents of maps
     */
    public static void clean() {
        Maps.USER_MAP.clear();
        Maps.ACCOUNT_MAP.clear();
        Maps.CARD_MAP.clear();
        Maps.ALIAS_MAP.clear();
        ExchangeRate.DIST.clear();
        ExchangeRate.CURRENCIES.clear();
    }
}
