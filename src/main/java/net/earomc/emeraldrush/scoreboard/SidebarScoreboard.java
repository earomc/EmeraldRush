package net.earomc.emeraldrush.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A bukkit scoreboard wrapper that allows for easier creation and updates of display type scoreboards which are used on most servers.
 * Setting/adding/removing {@link SidebarLine}s, updating {@link SidebarLine} objects with new info/parameters/data requires an update of the lines you changed
 * or in case of reordering of the lines (for example through adding lines at the bottom with the standard {@link SidebarScoreboard#addLine(SidebarLine)} method) requires
 * updating the entire scoreboard with the {@link SidebarScoreboard#rebuild()} method.
 * <p>
 * This implementation does not contain a player. It is intended that this scoreboard is updated and edited through an instance of {@link SidebarScoreboardManager},
 * as it tracks player's scoreboard instances and assures clean-up after the scoreboard is removed from the player. The {@link SidebarScoreboardManager}
 * makes the scoreboard visible to the player when it's set and automatically removes the display if the scoreboard removed.
 * UPDATE THE SCOREBOARD VIA {@link SidebarScoreboardManager} AND NEVER THROUGH THE INTERNAL UPDATE METHODS
 */

public class SidebarScoreboard {

    private final String displayName;
    private final ArrayList<SidebarLine> linesList;
    private Scoreboard bukkitScoreboard;
    private Objective objective;

    /**
     * @param displayName The title of the scoreboard or more precisely, the displayName of the objective that is registered.
     */
    public SidebarScoreboard(@NotNull String displayName) {
        if (displayName.length() > 32)
            throw new IllegalArgumentException("displayName cannot be longer than " + 32 + " characters");
        this.displayName = displayName;
        this.linesList = new ArrayList<>();
    }

    /**
     * Initializes the internal bukkit scoreboard and objective and applies lines in the linesList to them.
     * Sets the values stored in the line objects to the actual bukkit scoreboard.
     */
    protected void build() {
        this.bukkitScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.objective = bukkitScoreboard.registerNewObjective("objective", "dummy");
        this.objective.setDisplayName(displayName);
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        for (SidebarLine line : linesList) {
            fastBuildLine(line);
        }
    }

    public SidebarScoreboard(@NotNull String displayName, SidebarLine... lines) {
        this(displayName);
        addLines(lines);
    }

    public boolean hasLine(SidebarLine line) {
        if (line == null) return false;
        return linesList.contains(line);
    }

    /**
     * Adds the given array of lines at the bottom of the scoreboard.
     *
     * @param lines The lines you want to add.
     */
    public void addLines(SidebarLine... lines) {
        Arrays.stream(lines).forEach(this::addLine);
    }

    /**
     * Adds a line at the bottom of the scoreboard. Shifts every other line one up.
     *
     * @param line The line you want to set at the index.
     */
    public void addLine(SidebarLine line) {
        if (linesList.isEmpty()) {
            linesList.add(line);
        } else addLine(0, line);
    }

    /**
     * Adds a line at index with the given index. While doing this it shifts every element after the index one to the right. (if the list starts on the left)
     *
     * @param index index
     * @param line  The line you want to set at the index.
     */
    public void addLine(int index, SidebarLine line) {
        validateNewLineIndex(index);
        linesList.add(index, line);
        // performance improvement idea: check if line has been added as last index and if so, don't reapply all index but only set the line's index to index arg.
        applyListIndexesToLines();
    }

    /**
     * Removes a line at the given index. While doing this it shifts every element after the index one to the left. (if the list starts on the left)
     * Requires a rebuild. See: {@link #rebuild()}
     *
     * @param index index
     */
    public void removeLine(int index) {
        validateLineIndexExists(index);
        SidebarLine removed = linesList.remove(index);
        if (removed != null) {
            unregisterLine(removed);
            applyListIndexesToLines();
        }
    }

    /**
     * Replaces any line at the given index if present. Requires a scoreboard rebuild. See: {@link #rebuild()}
     * If used without redraw, in case of it replacing another line instance, the replaced instance cannot be removed.
     * Sets line to the given index if valid.
     *
     * @param index index
     * @param line  The line you want to set at the index.
     */
    public void setLine(int index, SidebarLine line) {
        validateNewLineIndex(index);
        if (index >= 0) {
            SidebarLine replaced = linesList.set(index, line);
            if (replaced != null) {
                unregisterLine(replaced);
            }
            line.setIndex(index);
        }
    }

    /**
     * Replaces a scoreboard line object with the given replacement. Requires a rebuild. See: {@link #rebuild()}
     *
     * @param toReplace   The line to replace.
     * @param replacement The replacement line.
     */

    public void replaceLine(SidebarLine toReplace, SidebarLine replacement) {
        setLine(toReplace.getIndex(), replacement);
    }

    /**
     * Unregisters the team and sets an invalid index. So that only lines that are actually visible in the scoreboard have registered teams and valid indexes.
     *
     * @param line The line to disable
     */
    // technically unnecessary because each method that uses this after modifying the linesList, requires a rebuild of the scoreboard anyway.
    private void unregisterLine(@NotNull SidebarLine line) {
        unregisterTeamIfPresent(line.getTeamName());
        line.setIndex(-1);
    }

    /**
     * When adding a new Line that changes the structure of the list, (e.g. shifting elements back or forth), this method is called
     * to assure that every {@link SidebarLine} has the correct index set to it.
     */
    private void applyListIndexesToLines() {
        for (int i = 0; i < linesList.size(); i++) {
            SidebarLine current = linesList.get(i);
            current.setIndex(i);
        }
    }

    /**
     * Updates all line object's prefixes and/or suffixes to the bukkit scoreboard.
     * Don't use this method if you change the structure of the scoreboard (adding, removing, replacing, renaming line entries or teams).
     * In that case, use {@link #rebuild()}.
     */
    void updateLines() {
        for (SidebarLine line : linesList) {
            updateLine(line);
        }
    }

    /**
     * Updates the internal bukkit {@link Scoreboard} with all internal {@link SidebarLine}s.
     * Required after changing the structure of the scoreboard (adding, removing, replacing, renaming line entries or teams)
     * If you wouldn't do a rebuild in this case, the lines that were attempted to be removed or replaced would persist in the internal bukkit scoreboard and objective.
     * That's because you cannot individually delete or overwrite entries in Bukkit scoreboards ... ik it's dam stupid.
     * So this method completely reconstructs the scoreboard based on the SidebarLine objects contained inside the line list. Meaning it creates a new internal Bukkit scoreboard and registers
     * everything again.
     */
    void rebuild() {
        unregister();
        build();
    }

    /**
     * Updates the given line's prefix and/or suffix.
     * @param index The index of the line to update.
     */
    void updateLine(int index) {
        validateLineIndexExists(index);
        updateLine(getLine(index));
    }

    /**
     * Updates the given line's prefix and/or suffix.
     * @param line The line to update.
     */
    void updateLine(SidebarLine line) {
        if (!linesList.contains(line))
            throw new IllegalStateException("This scoreboard doesn't contain the line you tried to update");
        if (line.usesTeam()) {
            Team team = getTeamRegisterIfAbsent(line.getTeamName());
            if (line.getPrefix() != null) team.setPrefix(line.getPrefix());
            if (line.getSuffix() != null) team.setSuffix(line.getSuffix());
        } else throw new IllegalArgumentException("Cannot update line that doesn't use teams like using this method. Set a new line and rebuild the scoreboard instead");
    }

    /**
     * Sets the values stored in the line object to the actual bukkit scoreboard if the line object is contained in the linesList.
     * Else it will throw an {@link IllegalStateException}
     *
     * @param line The line to set to the scoreboard.
     */
    void buildLine(SidebarLine line) {
        if (!linesList.contains(line))
            throw new IllegalStateException("This scoreboard doesn't contain the line you want to update");
        fastBuildLine(line);
    }

    /**
     * Like {@link #buildLine(SidebarLine)} without list contains check.
     */
    private void fastBuildLine(SidebarLine line) {
        if (line.usesTeam()) {
            Team team = getTeamRegisterIfAbsent(line.getTeamName());
            team.getEntries().forEach(team::removeEntry); // test if this works
            team.addEntry(line.getEntry());

            if (line.getPrefix() != null) team.setPrefix(line.getPrefix());
            if (line.getSuffix() != null) team.setSuffix(line.getSuffix());
        }
        objective.getScore(line.getEntry()).setScore(line.getIndex());
    }

    /**
     * Returs the index of the given line or -1 if the line isn't contained in the scoreboard.
     *
     * @param line A line to get the index from.
     * @return The index of the given line or -1 if the line isn't contained in the scoreboard.
     */
    public int indexOf(SidebarLine line) {
        return linesList.indexOf(line);
    }

    private Team getTeamRegisterIfAbsent(String teamName) {
        Team team = bukkitScoreboard.getTeam(teamName);
        if (team == null) {
            team = bukkitScoreboard.registerNewTeam(teamName);
        }
        return team;
    }

    /**
     * If the team with the given name is present, it unregisters it.
     *
     * @param teamName The name of the team.
     * @return True if team was unregistered. False if not.
     */
    private boolean unregisterTeamIfPresent(String teamName) {
        Team team = bukkitScoreboard.getTeam(teamName);
        if (team != null) {
            team.unregister();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Gets a line at the specified index.
     *
     * @param index The index
     * @return The line at that index
     */
    @NotNull
    public SidebarLine getLine(int index) {
        return linesList.get(index);
    }

    /**
     * Gets a line by its team name. See: {@link Team}
     *
     * @param teamName The line's team name.
     * @return The line with the given team name exists in the line list, {@code null} if not.
     */
    @Nullable
    public SidebarLine getLineByTeamName(String teamName) {
        return linesList.stream()
                .filter(line -> line.getTeamName() != null && line.getTeamName().equals(teamName))
                .findFirst()
                .orElse(null);
    }

    /**
     * Gets a line by its entry. {@link Team#getEntries()} (String)}
     *
     * @param entry The line's entry.
     * @return The line with the given entry exists in the line list, {@code null} if not.
     */
    @Nullable
    public SidebarLine getLineByEntry(String entry) {
        return linesList.stream()
                .filter(line -> line.getEntry().equals(entry))
                .findFirst()
                .orElse(null);
    }

    /**
     * @return Returns the internal bukkit scoreboard.
     */
    public Scoreboard getBukkitScoreboard() {
        return bukkitScoreboard;
    }

    /**
     * @return Returns the scoreboard's / objective's display name.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Unregisters all teams and the objective that has been registered for this scoreboard.
     */
    public void unregister() {
        // unregister teams
        for (SidebarLine line : linesList) {
            String teamName = line.getTeamName();
            if (teamName == null) continue;
            unregisterTeamIfPresent(teamName);
        }
        // unregister objective
        objective.unregister();
    }

    /**
     * Makes sure that when setting or adding a new line, the index is valid.
     *
     * @param index index.
     */
    protected static void validateNewLineIndex(int index) {
        if (index < 0) throw new IllegalArgumentException("Line index has to be above or equal to 0. Actual: " + index);
    }

    /**
     * Makes sure that the given line index exists in the lines list
     *
     * @param index index.
     */
    protected void validateLineIndexExists(int index) {
        validateLineIndexExists(index, "No line at index " + index + " in scoreboard " + this.displayName);
    }

    /**
     * Makes sure that the given line index exists in the lines list
     *
     * @param message A custom message that is sent passed on to the exception.
     * @param index   index.
     */
    protected void validateLineIndexExists(int index, String message) {
        if (index < 0 || index >= linesList.size()) throw new IllegalArgumentException(message);
    }
}

/*
        Scoreboard
            - Teams
            - Objective
            Scoreboard can hold objectives.
            An Objective is what is displayed in the end.

            In vanilla Minecraft there can only be one scoreboard, but with Bukkit, you can create multiple and switch between them.

        Entry
            Represented as a String. Either a custom entry or historically a player name.

            They are unique for the objective.
            Is stored in teams, but an objective stores teams and scoreboards store objectives,
            so they can be accessed from anywhere in the scoreboard.

            Allows to set a score to. So each entry has one score.
            The name string of the entry will be displayed in the scoreboard.
        Score
            A score is just a number that is attributed to a specific entry.
            The score dictates the ordering of the scoreboard lines.
            The higher the score, the higher the line is in the scoreboard.


        Objective
            Objectives are a collection of scores that each belong to a specific entry / player name
            contains:
            - A name (can only be seen internally)

            - A "criterion" (in our case it's mostly just "dummy" or something)
              Minecraft uses these "criteria" to update the scoreboard scores (for example when breaking a specific block).
              We do that manually in code.

            - displayName (The title of the objective that will be displayed as title in the scoreboard)

        Team
           A team is a set of entries. Each entry has a score attributed to it.
           In most our cases, we only use one entry per team though. Because we only use the team to add suffixes and prefixes
            - are registered and created from/at a scoreboard

            contain:
            - Name (can only be seen internally)
            - entries / player names
              Entries are unique to each team. Meaning that one entry can only be in one team.
              Example: We have team1 and team2. When adding an entry to team2 while team1 already has that entry, it is removed from team1.
                       and added to team2.
            - prefix + suffix

         */
