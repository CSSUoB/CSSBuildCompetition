package com.cssbham.cssbuildcompetition.game.team;

import com.cssbham.cssbuildcompetition.game.Competition;
import com.cssbham.cssbuildcompetition.game.team.player.PlayerRegistry;
import com.plotsquared.core.plot.PlotId;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a team which is part of a {@link Competition}.
 */
public class Team {

    private final String name;
    private final Set<UUID> players;
    private final PlayerRegistry registry;
    private final PlotId plotId;
    private final int limit;

    private int score;

    /**
     * Creates a new instance of a team.
     * <p>
     * Teams should always be created using a {@link TeamManager}.
     * Do not use this constructor outside one, otherwise the created team will
     * be independent of the competition.
     *
     * @param name the name of this team
     * @param registry the registry of all participating players
     * @param plotId the plot id of the plot for this team
     * @param limit the maximum amount of players that can be in this team.
     *              This limit is not enforced by this class.
     */
    protected Team(String name, PlayerRegistry registry, PlotId plotId, int limit) {
        this.name = name;
        this.registry = registry;
        this.plotId = plotId;
        this.limit = limit;
        this.players = new HashSet<>();
    }

    /**
     * Gets a list of UUIDs of all players on this team.
     * Some players may not be online.
     *
     * @return collection of players on this team
     */
    public Set<UUID> getPlayers() {
        return Collections.unmodifiableSet(players);
    }

    /**
     * Gets a list of names of all players on this team.
     * Some players may not be online.
     *
     * @return collection of players on this team
     */
    public Set<String> getPlayersNames() {
        Set<String> players = new HashSet<>();
        for (UUID player : this.players) {
            players.add(Bukkit.getOfflinePlayer(player).getName());
        }
        return Collections.unmodifiableSet(players);
    }

    /**
     * Query whether a player is in this team.
     *
     * @param player the player to query
     * @return       true if the player is in this team, false otherwise
     */
    public boolean hasPlayer(UUID player) {
        return players.contains(player);
    }

    /**
     * Adds a player to this team. This also sends a message to the player
     * and notifies other players.
     *
     * @param player the UUID of the player to add
     * @return       false if the player is already on this team, true otherwise
     */
    public boolean addPlayer(UUID player) {
        if (registry.hasPlayer(player)) {
            return false;
        }
        Player joiner = Bukkit.getPlayer(player);
        if (joiner != null) {
            broadcastToTeam(Component.text(joiner.getName(), NamedTextColor.WHITE)
                    .append(Component.text(" has joined the team.", NamedTextColor.GREEN)));

            joiner.sendMessage(Component.text("You have joined ", NamedTextColor.GREEN)
                    .append(Component.text(name, NamedTextColor.WHITE))
                    .append(Component.text(".", NamedTextColor.GREEN)));
        }
        registry.addPlayer(player);
        players.add(player);
        return true;
    }

    /**
     * Removes a player from this team. This also sends a message to the player
     * and notifies other players.
     *
     * @param player the UUID of the player to add
     * @return       false if the player is not on this team, true otherwise
     */
    public boolean removePlayer(UUID player) {
        if (!registry.hasPlayer(player)) {
            return false;
        }
        Player leaver = Bukkit.getPlayer(player);
        registry.removePlayer(player);
        players.remove(player);
        if (leaver != null) {
            leaver.sendMessage(Component.text("You have left ", NamedTextColor.GREEN)
                    .append(Component.text(name, NamedTextColor.WHITE))
                    .append(Component.text(".", NamedTextColor.GREEN)));

            Component message = Component.text(leaver.getName(), NamedTextColor.WHITE)
                    .append(Component.text(" has left the team.", NamedTextColor.GREEN));
            broadcastToTeam(message);
        }
        return true;
    }

    /**
     * Broadcasts a message to players on this team.
     *
     * @param message the message to broadcast
     */
    public void broadcastToTeam(Component message) {
        for (UUID player : players) {
            Player p = Bukkit.getPlayer(player);
            if (p != null) {
                p.sendMessage(message);
            }
        }
    }

    /**
     * Gets the name of this team.
     *
     * @return the name of this team
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the plot ID for this team. The plot ID is selected and managed
     * by PlotSquared.
     *
     * @return the plot ID of this team
     */
    public PlotId getPlotId() {
        return plotId;
    }

    /**
     * Gets the score of this team.
     *
     * @return the score of this team
     */
    public int getScore() {
        return score;
    }

    /**
     * Sets the score of this team.
     *
     * @param score the score to set
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Get the player limit for this team. Player limits are not enforced by
     * the team manager.
     *
     * @return the player limit for this team
     */
    public int getLimit() {
        return limit;
    }
}
