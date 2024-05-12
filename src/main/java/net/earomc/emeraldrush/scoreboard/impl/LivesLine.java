package net.earomc.emeraldrush.scoreboard.impl;

import net.earomc.emeraldrush.scoreboard.SidebarLine;

public class LivesLine extends SidebarLine {
    private int lives;

    public LivesLine(int lives) {
        super("§aLives: ", "lives");
        this.lives = lives;
        this.setSuffix(" " + this.lives);
    }

}
