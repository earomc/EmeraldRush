package net.earomc.emeraldrush.scoreboard.impl;

import net.earomc.emeraldrush.scoreboard.SidebarScoreboard;
import net.earomc.emeraldrush.scoreboard.line.presets.SidebarLinePresets;

public class InGameScoreboard extends SidebarScoreboard {
    public InGameScoreboard() {
        super("EmeraldRush");
        addLine(SidebarLinePresets.SERVER_IP);
        addLine(SidebarLinePresets.EMPTIES[0]);
    }
}
