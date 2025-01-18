package org.poo.utils;

import java.util.Map;
import java.util.HashMap;

import org.poo.actors.Account;
import org.poo.actors.Commerciant;
import org.poo.actors.User;
import org.poo.actors.Card;

public final class Maps {
    private Maps() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static final Map<String, User> USER_MAP = new HashMap<>();
    public static final Map<String, Account> ACCOUNT_MAP = new HashMap<>();
    public static final Map<String, String> ALIAS_MAP = new HashMap<>();
    public static final Map<String, Card> CARD_MAP = new HashMap<>();
    public static final Map<String, Commerciant> COMM_MAP = new HashMap<>();
    public static final Map<String, Commerciant> COMM_ACCOUNT_MAP = new HashMap<>();
}
