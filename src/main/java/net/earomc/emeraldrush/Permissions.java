package net.earomc.emeraldrush;

import org.bukkit.permissions.Permission;

public class Permissions {

    private static String withPrefix(String perm) {
        return "emeraldrush." + perm;
    }

    public static final Permission JOIN_DURING_GAME = new Permission(withPrefix("join.duringgame"));

}
