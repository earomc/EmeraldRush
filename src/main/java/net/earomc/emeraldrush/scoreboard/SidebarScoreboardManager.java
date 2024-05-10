package net.earomc.emeraldrush.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class SidebarScoreboardManager<S extends SidebarScoreboard> {

    protected final Map<Player, S> scoreboardMap = new HashMap<>();

    public void setScoreboard(Player player, S scoreboard) {
        if (hasScoreboard(player)) removeScoreboard(player, false);
        scoreboardMap.put(player, scoreboard);
        scoreboard.build();
        player.setScoreboard(scoreboard.getBukkitScoreboard());
    }

    /**
     * Returns whether the player has an active scoreboard in this {@link SidebarScoreboardManager} instance.
     *
     * @param player The player you want to check for.
     * @return Returns {@code true} if the player has an active scoreboard in this SidebarScoreboardManager instance, {@code false} if not.
     */
    public boolean hasScoreboard(Player player) {
        return scoreboardMap.containsKey(player);
    }

    @Nullable
    public S getActiveScoreboard(Player player) {
        return scoreboardMap.get(player);
    }

    /**
     * @see #updateScoreboard(Player, Consumer, boolean)
     */
    public void updateScoreboardForAll(@NotNull Consumer<S> scoreboardUpdater, boolean rebuild) {
        this.scoreboardMap.keySet().forEach(player -> updateScoreboard(player, scoreboardUpdater, rebuild));
    }

    public <L extends SidebarLine> void updateScoreboardLineForAll(@NotNull Function<S, L> lineUpdater) {
        this.scoreboardMap.keySet().forEach(player -> updateScoreboardLine(player, lineUpdater));
    }

    /**
     * Updates the current player's scoreboard. The consumer consumes the current player's scoreboard object. If there's no
     * active scoreboard for that player, an {@link IllegalStateException} is thrown.
     * Updating in this case means.
     * @see SidebarScoreboard#rebuild()
     * @param player The player whose scoreboard is updated.
     * @param scoreboardUpdater A consumer which takes the player's current active scoreboard as input.
     * @param rebuild If true it will rebuild the Scoreboard with {@link SidebarScoreboard#rebuild()}. If false, it will
     *                update the scoreboard with {@link SidebarScoreboard#updateLines()}.
     */
    public void updateScoreboard(@NotNull Player player, @NotNull Consumer<S> scoreboardUpdater, boolean rebuild) {
        S activeScoreboard = getActiveScoreboard(player);
        if (activeScoreboard == null)
            throw new IllegalStateException("Player doesn't have an active scoreboard to update");
        scoreboardUpdater.accept(activeScoreboard);
        if (rebuild) {
            activeScoreboard.rebuild();
        } else {
            activeScoreboard.updateLines();
        }
        player.setScoreboard(activeScoreboard.getBukkitScoreboard());
    }

    /**
     * Updates one existing scoreboard {@link SidebarLine} using the lineUpdater function. The lineUpdater function takes in the given player's
     * current scoreboard and is supposed to return the SidebarLine that you updated. Updating a line meaning in this case, changing
     * its internal values. The updating of the visible scoreboard for the player is handled by this method itself.
     * You cannot replace lines with this method. In the lineUpdater function, only return the same line that you modified.
     * <p>
     * It's recommended that you have some sort of getter for individual SidebarLines inside your {@link SidebarScoreboard} implementation.
     * Alternatively you can have simple update methods in that each return the SidebarLine that you update with the given method.
     * This way you can easily pass in the lineUpdater with a simple one-line lambda expression.
     * @param player The player of which to update the scoreboard.
     * @param lineUpdater A function that takes a SidebarScoreboard as input and updates one of its lines.
     * @param <L> A {@link SidebarLine} subtype.
     */
    public <L extends SidebarLine> void updateScoreboardLine(@NotNull Player player, @NotNull Function<S, L> lineUpdater) {
        S activeScoreboard = getActiveScoreboard(player);
        if (activeScoreboard == null)
            throw new IllegalStateException("Player doesn't have an active scoreboard to update");
        L updatedLine = lineUpdater.apply(activeScoreboard);
        if (activeScoreboard.hasLine(updatedLine)) {
            activeScoreboard.updateLine(updatedLine);
            player.setScoreboard(activeScoreboard.getBukkitScoreboard());
        }
    }

    @Nullable
    public S removeScoreboard(Player player) {
        return removeScoreboard(player, true);
    }

    /**
     * Removes the scoreboard from the player and unregisters the teams and stuff associated with the scoreboard.
     *
     * @param player The player to remove the scoreboard from.
     * @param setNewEmptyScoreboard <p>If {@code true}: Sets a new bukkit scoreboard to the player and therefore clears the scoreboard display for the player. <p>
     *                         If {@code false}: Doesn't set a new bukkit scoreboard but still removes it from the map and unregisters the scoreboard.
     *                         can reduce flicker when immediately setting another scoreboard
     * @return The scoreboard if it was removed. It is unusable after that though because all teams and objectives were cleared. Can be used to check which scoreboard was removed.
     * The removed scoreboard still contains the display name
     */
    @Nullable
    public S removeScoreboard(Player player, boolean setNewEmptyScoreboard) {
        S removedScoreboard = scoreboardMap.remove(player);
        if (removedScoreboard != null) {
            removedScoreboard.unregister();
            if (setNewEmptyScoreboard) {
                player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            }
        }
        return removedScoreboard;
    }

}
