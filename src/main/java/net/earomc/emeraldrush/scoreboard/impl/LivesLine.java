package net.earomc.emeraldrush.scoreboard.impl;

import net.earomc.emeraldrush.scoreboard.SidebarLine;

public class LivesLine extends SidebarLine {
    private int lives = 0;

    public LivesLine(String entry, String teamName) {
        super("§aLives: ", "lives");
        this.setSuffix(" " + lives);
    }
}
