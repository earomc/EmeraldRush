package net.earomc.emeraldrush.scoreboard.line.presets;

import net.earomc.emeraldrush.scoreboard.SidebarLine;
import org.bukkit.ChatColor;

public class SidebarLinePresets {
    public static final SidebarLine[] LINES = new SidebarLine[ChatColor.values().length]; // meaning actual lines that are displayed on the scoreboard.
    // meaning actual lines that are displayed on the scoreboard.
    // each entry of this array can only be used once per scoreboard (objective)
    public static final SidebarLine[] LINES_LONG = new SidebarLine[ChatColor.values().length];
    public static final SidebarLine[] EMPTIES = new SidebarLine[ChatColor.values().length]; // empty lines.
    public static final SidebarLine SERVER_IP = new SidebarLine("§fearomc.net");

    static {
        initLines();
        initLinesLong();
        initEmpties();
    }

    private static void initLinesLong() {
        ChatColor[] chatColors = ChatColor.values();
        for (int i = 0; i < chatColors.length; i++) {
            String colorCode = chatColors[i].toString();

            SidebarLine line = new SidebarLine(colorCode + "§7§m---", "line" + i);
            line.setPrefix("§7§m------------");
            line.setSuffix("§7§m---");

            LINES_LONG[i] = line;
        }
    }

    private static void initLines() {
        ChatColor[] chatColors = ChatColor.values();
        for (int i = 0; i < chatColors.length; i++) {
            String colorCode = chatColors[i].toString();

            SidebarLine line = new SidebarLine(colorCode, "line" + i);
            line.setPrefix("§7§m------------");
            line.setSuffix("§7§m---");

            LINES[i] = line;
        }
    }

    private static void initEmpties() {
        ChatColor[] chatColors = ChatColor.values();
        for (int i = 0; i < chatColors.length; i++) {
            String colorCode = chatColors[i].toString();

            SidebarLine line = new SidebarLine(colorCode + "§0", "empty" + i);

            EMPTIES[i] = line;
        }
    }
}
