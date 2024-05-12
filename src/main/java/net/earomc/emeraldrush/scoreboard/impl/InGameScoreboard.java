package net.earomc.emeraldrush.scoreboard.impl;

import net.earomc.emeraldrush.scoreboard.SidebarScoreboard;
import net.earomc.emeraldrush.scoreboard.line.presets.SidebarLinePresets;
import net.earomc.emeraldrush.team.Team;

public class InGameScoreboard extends SidebarScoreboard {

    private final Team team;

    public InGameScoreboard(Team team) {
        super("EmeraldRush");
        this.team = team;
        addLine(SidebarLinePresets.SERVER_IP);
        addLine(SidebarLinePresets.EMPTIES[0]);
        addLine(new LivesLine(team.getLives()));
    }
}
