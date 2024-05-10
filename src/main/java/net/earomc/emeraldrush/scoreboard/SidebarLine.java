package net.earomc.emeraldrush.scoreboard;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The displayName is unique to the entire objective / {@link SidebarScoreboard} that it's used in. Internally it is a scoreboard entry name.
 * The name is the team's name. Can be initialized as null but then you cannot use
 */

public class SidebarLine {
    @NotNull
    private final String entry;
    @Nullable
    private final String teamName;
    @Nullable
    private String prefix;
    @Nullable
    private String suffix;

    private int index = -1;

    public SidebarLine(@NotNull String entry) {
        this(entry, null);
    }

    public SidebarLine(@NotNull String entry, @Nullable String teamName) {
        this.teamName = teamName;
        this.entry = verifyLength(entry, "entry");
    }

    public SidebarLine setPrefix(@Nullable String prefix) {
        if (!usesTeam())
            throw new IllegalStateException("Cannot use prefixes when not using teams / not initializing a name");
        this.prefix = verifyLength(prefix, "prefix");
        return this;
    }

    public SidebarLine setSuffix(@Nullable String suffix) {
        if (!usesTeam()) throw new IllegalStateException("Cannot use suffixes when not using teams / not initializing a name");
        this.suffix = verifyLength(suffix, "suffix");
        return this;
    }

    /**
     * team name
     * The {@link org.bukkit.scoreboard.Team} name this line is represented by. Not the name displayed.
     *
     * @return The internal name of this line/the scoreaboard team name.
     */
    @Nullable
    public String getTeamName() {
        return teamName;
    }

    /**
     * display name and entry
     * The name this line is displayed with, but also the name of the entry that is added to the team for this line.
     *
     * @return The name this line is displayed with without the suffix or prefix or the entry name.
     */
    @NotNull
    public String getEntry() {
        return entry;
    }

    @Nullable
    public String getPrefix() {
        return prefix;
    }

    @Nullable
    public String getSuffix() {
        return suffix;
    }

    public boolean usesTeam() {
        return teamName != null;
    }

    private static String verifyLength(String string, String varName) {
        if (string != null && string.length() > 16)
            throw new IllegalArgumentException(varName + " \"" + string + "\" cannot be longer than " + 16 + " characters");
        return string;
    }

    void setIndex(int index) {
        this.index = index;
    }

    int getIndex() {
        return index;
    }
}