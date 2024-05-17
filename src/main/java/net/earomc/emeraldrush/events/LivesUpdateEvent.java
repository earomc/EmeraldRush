package net.earomc.emeraldrush.events;

import net.earomc.emeraldrush.team.Team;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LivesUpdateEvent extends Event {
    private final Team team;
    private int before;
    private int now;

    private static final HandlerList handlerList = new HandlerList();
    public LivesUpdateEvent(Team team, int before, int now) {
        this.team = team;
        this.before = before;
        this.now = now;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public Team getTeam() {
        return team;
    }

    public int getBefore() {
        return before;
    }

    public int getNow() {
        return now;
    }
}
