package com.cssbham.cssbuildcompetition.util;

import com.cssbham.cssbuildcompetition.BuildCompetitionPlugin;
import com.cssbham.cssbuildcompetition.exception.CompetitionInProgressException;
import com.cssbham.cssbuildcompetition.exception.CompetitionNotRunningException;
import com.cssbham.cssbuildcompetition.exception.PlayerNotInTeamException;
import com.cssbham.cssbuildcompetition.game.team.Team;
import org.bukkit.entity.Player;

public class CommandPreconditions {

    /**
     * Requires a competition to not be running.
     *
     * @param plugin the plugin instance
     * @throws CompetitionInProgressException if the competition is running
     */
    public static void requireNoCompetition(BuildCompetitionPlugin plugin) {
        if (plugin.getCompetition() != null && plugin.getCompetition().isRunning()) {
            throw new CompetitionInProgressException();
        }
    }

    /**
     * Requires a competition to be running.
     *
     * @param plugin the plugin instance
     * @throws CompetitionNotRunningException if the competition is not running
     */
    public static void requireCompetition(BuildCompetitionPlugin plugin) {
        if (plugin.getCompetition() == null || !plugin.getCompetition().isRunning()) {
            throw new CompetitionNotRunningException();
        }
    }

    /**
     * Requires a player to be in a team.
     *
     * @param plugin the plugin instance
     * @param player the player to check
     * @throws PlayerNotInTeamException if the player is not in a team
     */
    public static void requireTeam(BuildCompetitionPlugin plugin, Player player) {
        if (plugin.getCompetition() == null) {
            throw new PlayerNotInTeamException();
        }

        Team team = plugin.getCompetition().getTeamManager().getTeamOfPlayer(player.getUniqueId());
        if (!team.getPlayers().contains(player.getUniqueId())) {
            throw new PlayerNotInTeamException();
        }
    }

}
