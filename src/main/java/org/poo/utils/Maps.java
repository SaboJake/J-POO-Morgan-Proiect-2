package org.poo.utils;

import java.util.Map;
import java.util.HashMap;

import org.poo.actors.User;
import org.poo.actors.Account;
import org.poo.actors.Card;
import org.poo.actors.Commerciant;
import org.poo.actors.ServicePlan;

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
    public static final Map<String, ServicePlan> PLAN_MAP = Map.ofEntries(
            Map.entry("standard", ServicePlan.STANDARD),
            Map.entry("student", ServicePlan.STUDENT),
            Map.entry("silver", ServicePlan.SILVER),
            Map.entry("gold", ServicePlan.GOLD)
    );

    public static final int[][] UPGRADE_MATRIX = {
        {0, 0, 100, 350},
        {0, 0, 100, 350},
        {0, 0, 0, 250},
        {0, 0, 0, 0}
    };
}
