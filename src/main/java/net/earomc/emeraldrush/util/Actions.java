package net.earomc.emeraldrush.util;

import org.bukkit.event.block.Action;

/**
 * @author earomc
 * Created on Dezember 08, 2021 | 06:07:43
 * ʕっ•ᴥ•ʔっ
 */

public class Actions {
    public static boolean isRightClick(Action action) {
        return action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK;
    }

    public static boolean isLeftClick(Action action) {
        return action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK;
    }

    public static boolean isBlockClick(Action action) {
        return action == Action.RIGHT_CLICK_BLOCK || action == Action.LEFT_CLICK_BLOCK;
    }

    public static boolean isAirClick(Action action) {
        return action == Action.RIGHT_CLICK_AIR || action == Action.LEFT_CLICK_AIR;
    }

    public static boolean isClick(Action action) {
        return action == Action.RIGHT_CLICK_AIR || action == Action.LEFT_CLICK_AIR
                || action == Action.RIGHT_CLICK_BLOCK || action == Action.LEFT_CLICK_BLOCK;
    }
}
