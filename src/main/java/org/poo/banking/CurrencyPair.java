package org.poo.banking;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter @AllArgsConstructor
public class CurrencyPair {
    private final String from;
    private final String to;

    private static final int HASH_COEF = 31;

    /**
     * Check if obj is equal to this
     * @param obj - object to be checked
     * @return - true if equal, false otherwise
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        CurrencyPair pair = (CurrencyPair) obj;
        return from.equals(pair.from) && to.equals(pair.to);
    }

    /**
     * Simple hash function for a pair of strings
     * @return - new hash
     */
    @Override
    public int hashCode() {
        return from.hashCode() * HASH_COEF + to.hashCode();
    }
}
