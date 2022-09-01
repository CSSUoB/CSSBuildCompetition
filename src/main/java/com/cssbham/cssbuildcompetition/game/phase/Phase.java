package com.cssbham.cssbuildcompetition.game.phase;

import com.cssbham.cssbuildcompetition.BuildCompetitionPlugin;
import com.cssbham.cssbuildcompetition.game.Competition;
import com.cssbham.cssbuildcompetition.game.command.CommandRouter;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

/**
 * Represents a phase in a {@link Competition}.
 */
public abstract class Phase implements Listener {

    private static final Logger logger;

    static {
        logger = BuildCompetitionPlugin.getPlugin(BuildCompetitionPlugin.class).getLogger();
    }

    /**
     * The name of this phase
     */
    private final String name;

    public Phase(@NotNull String name) {
        this.name = name;
    }

    /**
     * Gets the name of this phase.
     *
     * @return the name of this phase
     */
    public @NotNull String getName() {
        return name;
    }

    /**
     * Ticks this game phase.
     *
     * @return true if the phase has finished, false otherwise.
     */
    public abstract boolean tick();

    /**
     * Called only once when this phase is started.
     */
    public void start() {
        // no impl
    }

    /**
     * Called only once when this phase is ending.
     */
    public void end() {
        // no impl
    }

    /**
     * Called when this phase should register its commands
     * with the competition's {@link CommandRouter}.
     */
    public void registerCommands(@NotNull CommandRouter commandRouter) {
        // no impl
    }

    public final void logInfo(@NotNull String message) {
        logger.info("[" + name + "] " + message);
    }

    public final void logWarning(@NotNull String message) {
        logger.warning("[" + name + "] " + message);
    }

    public final void logSevere(@NotNull String message) {
        logger.severe("[" + name + "] " + message);
    }

}
