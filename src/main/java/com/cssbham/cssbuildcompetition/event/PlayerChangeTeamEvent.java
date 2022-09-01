package com.cssbham.cssbuildcompetition.event;

import com.cssbham.cssbuildcompetition.game.team.Team;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Event that is fired after a player changes team.
 */
public class PlayerChangeTeamEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final UUID player;
    private final Team oldTeam;
    private final Team newTeam;

    public PlayerChangeTeamEvent(@NotNull UUID player, @Nullable Team oldTeam, @Nullable Team newTeam) {
        this.player = player;
        this.oldTeam = oldTeam;
        this.newTeam = newTeam;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public @NotNull UUID getPlayer() {
        return player;
    }

    /**
     * Gets the old team the player was in.
     * This will be null if the player wasn't in a team.
     *
     * @return the old team, or null
     */
    public @Nullable Team getOldTeam() {
        return oldTeam;
    }

    /**
     * Gets the new team the player is in.
     * This will be null if the player did not join another team.
     *
     * @return the new team, or null
     */
    public @Nullable Team getNewTeam() {
        return newTeam;
    }

    public static PlayerChangeTeamEvent dispatchEvent(@NotNull UUID player, @Nullable Team oldTeam, @Nullable Team newTeam) {
        PlayerChangeTeamEvent event = new PlayerChangeTeamEvent(player, oldTeam, newTeam);
        event.callEvent();
        return event;
    }
}
