package com.cssbham.cssbuildcompetition.game;

import com.cssbham.cssbuildcompetition.BuildCompetitionPlugin;
import com.cssbham.cssbuildcompetition.game.command.CommandRouter;
import com.cssbham.cssbuildcompetition.game.phase.*;
import com.cssbham.cssbuildcompetition.game.team.TeamManager;
import com.cssbham.cssbuildcompetition.game.team.player.PlayerRegistry;
import com.plotsquared.core.plot.PlotArea;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Represents a competition, which is a collection of teams and phases.
 */
public class Competition {

    private final BuildCompetitionPlugin plugin;

    private final Queue<Phase> upcomingPhases;
    private Phase currentPhase;
    private final PlayerRegistry playerRegistry;
    private final TeamManager teamManager;
    private final CommandRouter commandRouter;

    private BukkitRunnable phaseTicker;
    private boolean running;

    public Competition(BuildCompetitionPlugin plugin, PlotArea plotArea, Options options) {
        this.plugin = plugin;

        this.playerRegistry = new PlayerRegistry();
        this.commandRouter = new CommandRouter();
        this.teamManager = new TeamManager(playerRegistry, plotArea, options.getMaxTeamSize());

        upcomingPhases = new LinkedList<>();
        upcomingPhases.add(new SetupPhase(teamManager));
        upcomingPhases.add(new BuildPhase(teamManager, options));
        upcomingPhases.add(new VotePhase(teamManager, options));
        upcomingPhases.add(new ResultPhase(teamManager, options));
    }

    /**
     * Gets if this player is in the competition.
     *
     * @return true if the player is present on a team, false otherwise
     */
    public boolean isPlayerInCompetition(UUID player) {
        return teamManager.isPlayerInCompetition(player);
    }

    /**
     * Gets the {@link TeamManager} for this competition.
     *
     * @return the team manager
     */
    public @NotNull TeamManager getTeamManager() {
        return teamManager;
    }

    /**
     * Gets whether this competition is currently running. A competition is
     * running if there is at least one phase in the queue, or if there is
     * an active phase.
     *
     * @return true if the competition is running, false otherwise
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Gets the current phase of this competition. If no phase is currently
     * running, null is returned.
     *
     * @return the current phase, or null if no phase is currently running
     */
    public @Nullable Phase getCurrentPhase() {
        return currentPhase;
    }

    /**
     * Gets the upcoming phases of this competition.
     *
     * @return the upcoming phases
     */
    public @NotNull Collection<Phase> getUpcomingPhases() {
        return Collections.unmodifiableCollection(upcomingPhases);
    }

    /**
     * Gets the command router for competition commands.
     *
     * @return the command router
     */
    public CommandRouter getCommandRouter() {
        return commandRouter;
    }

    /**
     * Starts this competition.
     */
    public void start() {
        if (phaseTicker != null) {
            phaseTicker.cancel();
        }

        phaseTicker = new BukkitRunnable() {
            @Override
            public void run() {
                if (currentPhase != null) {
                    if (currentPhase.tick()) {
                        advancePhase();
                    }
                } else {
                    running = false;
                    this.cancel();
                }
            }
        };

        running = true;
        advancePhase();
        phaseTicker.runTaskTimer(plugin, 0, 20);
    }

    /**
     * Stops this competition.
     */
    public void stop() {
        running = false;
        if (phaseTicker != null) {
            phaseTicker.cancel();
        }
    }

    /**
     * Advances to the next phase in this competition. If there are no more phases,
     * the competition is eventually stopped.
     *
     * @return the next phase, or null if there are no more phases
     */
    public @Nullable Phase advancePhase() {
        Phase phase = upcomingPhases.poll();

        if (currentPhase != null) {
            currentPhase.end();
            HandlerList.unregisterAll(currentPhase);
        }

        if (phase != null) {
            if (currentPhase == null) {
                plugin.getLogger().info(String.format("Starting phase \"%s\"", phase.getName()));
            } else {
                plugin.getLogger().info(String.format("Changing phase from \"%s\" -> \"%s\"", currentPhase.getName(), phase.getName()));
            }
            commandRouter.unregisterCommands();
            phase.start();
            phase.registerCommands(commandRouter);
            Bukkit.getPluginManager().registerEvents(phase, plugin);
            currentPhase = phase;
            return phase;
        } else {
            plugin.getLogger().info(String.format("Ending phase \"%s\"", currentPhase.getName()));
            currentPhase = null;
        }
        return null;
    }
}
