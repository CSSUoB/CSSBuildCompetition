package com.cssbham.cssbuildcompetition.game.team.player;

import com.cssbham.cssbuildcompetition.game.Competition;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Registers which players are participating in a {@link Competition}.
 */
public class PlayerRegistry {

    /**
     * The players present in this registry.
     */
    private final Set<UUID> players = new HashSet<>();

    /**
     * Gets all players in this registry.
     */
    public @NotNull Set<UUID> getPlayers() {
        return Collections.unmodifiableSet(players);
    }

    /**
     * Adds a player to this registry.
     *
     * @param player the player to add
     * @return       true if the player was added, false otherwise
     */
    public boolean addPlayer(UUID player) {
        return players.add(player);
    }

    /**
     * Removes a player from this registry.
     *
     * @param player the player to remove
     * @return       true if the player was removed, false otherwise
     */
    public boolean removePlayer(UUID player) {
        return players.remove(player);
    }

    /**
     * Gets if this player is in this registry.
     *
     * @return true if the player is present, false otherwise
     */
    public boolean hasPlayer(UUID player) {
        return players.contains(player);
    }
}
