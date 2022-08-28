package com.cssbham.cssbuildcompetition.game.phase;

import com.cssbham.cssbuildcompetition.game.Competition;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a phase in a {@link Competition}.
 */
public abstract class Phase implements Listener {

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

}
