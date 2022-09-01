package com.cssbham.cssbuildcompetition.game.team;

import com.cssbham.cssbuildcompetition.game.team.player.PlayerRegistry;
import com.plotsquared.core.player.ConsolePlayer;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.PlotArea;
import com.plotsquared.core.plot.flag.implementations.ServerPlotFlag;

import java.util.*;

/**
 * Manages team registrations and player registrations.
 */
public class TeamManager {

    private final PlayerRegistry playerRegistry;
    private final List<Team> teams;
    private final PlotArea plotArea;
    private boolean locked;
    private final int limit;

    public TeamManager(PlayerRegistry playerRegistry, PlotArea plotArea, int limit) {
        this.playerRegistry = playerRegistry;
        this.plotArea = plotArea;
        this.limit = limit;
        teams = new ArrayList<>();
    }

    /**
     * Creates a new team and registers it with this competition.
     *
     * @return the newly created team
     */
    public Team createNewTeam() {
        Plot plot = plotArea.getNextFreePlot(ConsolePlayer.getConsole(), null);
        plot.claim(ConsolePlayer.getConsole(), false, null, true, false);
        plot.setFlag(ServerPlotFlag.SERVER_PLOT_TRUE);

        Team team = new Team("Team " + (teams.size() + 1), playerRegistry, plot.getId(), limit);
        teams.add(team);
        return team;
    }

    /**
     * Adds a player to any available team. A team is available is there
     * are 0 players on it. If no teams are available, a new team is created
     * and the player added.
     *
     * @param player the player to add
     * @return       the team the player was added to, or null if they are in one
     */
    public Team addPlayerToAvailableTeam(UUID player) {
        if (isPlayerInCompetition(player)) {
            return null;
        }
        for (Team team : teams) {
            if (team.getPlayers().size() == 0) {
                team.addPlayer(player);
                return team;
            }
        }
        Team team = createNewTeam();
        team.addPlayer(player);
        return team;
    }

    /**
     * Gets the team of this player. If the player is not on a team,
     * <code>null</code> is returned instead.
     *
     * @param player the player to get
     * @return       the team of the player, or <code>null</code> if the player
     *               is not on a team
     */
    public Team getTeamOfPlayer(UUID player) {
        for (Team team : teams) {
            if (team.hasPlayer(player)) {
                return team;
            }
        }
        return null;
    }

    /**
     * Gets all teams in this competition.
     *
     * @return a list of teams in this competition
     */
    public List<Team> getTeams() {
        return Collections.unmodifiableList(teams);
    }

    /**
     * Gets if this player is in the competition.
     *
     * @return true if the player is present on a team, false otherwise
     */
    public boolean isPlayerInCompetition(UUID player) {
        return playerRegistry.hasPlayer(player);
    }

    /**
     * Removes empty teams from the competition.
     *
     * @return the number of teams removed
     */
    public int pruneTeams() {
        int size = teams.size();
        teams.removeIf(team -> team.getPlayers().size() == 0);
        return teams.size() - size;
    }

    /**
     * Gets the player registry. The player registry is for quick access to players
     * participating in this competition. It should be used over iterating all teams
     * to check if a player is participating.
     *
     * @return the player registry
     */
    public PlayerRegistry getPlayerRegistry() {
        return playerRegistry;
    }

    /**
     * Locks the teams. This has no effect other than making {@link #isLocked()}
     * return true.
     */
    public void lockTeams() {
        locked = true;
    }

    /**
     * Gets if the teams are locked. If the teams are locked then players should
     * be prohibited from leaving or joining new teams.
     *
     * @return true if the teams are locked, false otherwise
     */
    public boolean isLocked() {
        return locked;
    }
}
