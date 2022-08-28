package com.cssbham.cssbuildcompetition.game.phase;

import com.cssbham.cssbuildcompetition.game.Options;
import com.cssbham.cssbuildcompetition.game.team.Team;
import com.cssbham.cssbuildcompetition.game.team.TeamManager;
import com.cssbham.cssbuildcompetition.util.PlotSquaredHelper;
import com.cssbham.cssbuildcompetition.util.TimeFormat;
import com.plotsquared.core.PlotSquared;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.PlotArea;
import com.plotsquared.core.plot.flag.implementations.ServerPlotFlag;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class BuildPhase extends Phase {

    private final TeamManager teamManager;
    private final PlotArea plotArea;
    private final long duration;
    private final String theme;

    private long endTime;
    private BossBar timer;

    public BuildPhase(TeamManager teamManager, Options options) {
        super("build");

        this.duration = TimeUnit.MILLISECONDS.convert(options.getBuildTime(), TimeUnit.SECONDS);
        this.plotArea = PlotSquared.get().getPlotAreaManager().getPlotArea(options.getPlotworld(), null);
        this.teamManager = teamManager;
        this.theme = options.getTheme();
    }

    @Override
    public void start() {
        endTime = System.currentTimeMillis() + duration;
        timer = BossBar.bossBar(Component.text("XX remaining"),
                1, BossBar.Color.PURPLE, BossBar.Overlay.NOTCHED_10);

        Sound sound = Sound.sound(Key.key("block.note_block.pling"),
                Sound.Source.NEUTRAL, 2, 2);
        Title title = Title.title(Component.text("Start building!", NamedTextColor.GREEN),
                Component.text("Theme: " + theme, NamedTextColor.GRAY),
                Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(5000), Duration.ofMillis(500)));

        for (Team team : teamManager.getTeams()) {
            Plot plot = plotArea.getPlot(team.getPlotId());
            plot.setFlag(ServerPlotFlag.SERVER_PLOT_TRUE);
            for (UUID uuid : team.getPlayers()) {
                plot.addTrusted(uuid);
            }
            plot.getCenter((centre) -> {
                for (UUID uuid : team.getPlayers()) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null) {
                        player.teleport(PlotSquaredHelper.convertPlotSquaredLocationToBukkitLocation(centre));
                        player.showTitle(title);
                        player.playSound(sound, Sound.Emitter.self());
                    }
                }
            });
        }
    }

    @Override
    public void end() {
        for (UUID uuid : teamManager.getPlayerRegistry().getPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.hideBossBar(timer);
            }
        }

        for (Team team : teamManager.getTeams()) {
            Plot plot = plotArea.getPlot(team.getPlotId());
            for (UUID uuid : team.getPlayers()) {
                plot.removeTrusted(uuid);
            }
        }
    }

    @Override
    public boolean tick() {
        long timeRemaining = endTime - System.currentTimeMillis();
        if (timeRemaining <= 0) {
            return true;
        }

        timer.name(Component.text(TimeFormat.convertToHumanReadableTime(timeRemaining) + " remaining"));
        timer.progress((float) timeRemaining / duration);

        for (UUID uuid : teamManager.getPlayerRegistry().getPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.showBossBar(timer);
            }
        }
        return false;
    }

}
