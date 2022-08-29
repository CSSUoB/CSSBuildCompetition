package com.cssbham.cssbuildcompetition;

import com.cssbham.cssbuildcompetition.command.BuildCompetitionAdminCommand;
import com.cssbham.cssbuildcompetition.command.CompetitionCommand;
import com.cssbham.cssbuildcompetition.command.TeamCommand;
import com.cssbham.cssbuildcompetition.game.Competition;
import com.cssbham.cssbuildcompetition.game.Options;
import com.plotsquared.core.PlotSquared;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

public class BuildCompetitionPlugin extends JavaPlugin {

    private PlotSquared plotSquared;
    private Competition competition;

    @Override
    public void onEnable() {
        this.plotSquared = PlotSquared.get();

        super.getCommand("team").setExecutor(new TeamCommand(this));
        super.getCommand("competition").setExecutor(new CompetitionCommand(this));
        super.getCommand("buildcompetitionadmin").setExecutor(new BuildCompetitionAdminCommand(this));

        ConfigurationSerialization.registerClass(Options.class);
        this.saveDefaultConfig();
    }

    /**
     * Gets the current competition. If there is no competition, null is returned.
     * A stopped competition may also be returned.
     *
     * @return the current competition, or null if there is no competition running
     */
    public @Nullable Competition getCompetition() {
        return competition;
    }

    /**
     * Starts a new competition. This will stop any current competition.
     */
    public void startNewCompetition() {
        reloadConfig();
        Options options = this.getConfig().getObject("options", Options.class);
        if (competition != null) {
            competition.stop();
        }
        competition = new Competition(this, plotSquared.getPlotAreaManager().getPlotArea(options.getPlotworld(), null), options);
        competition.start();
    }

}
