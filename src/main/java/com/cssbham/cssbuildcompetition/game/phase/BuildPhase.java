package com.cssbham.cssbuildcompetition.game.phase;

import com.cssbham.cssbuildcompetition.event.PlayerChangeTeamEvent;
import com.cssbham.cssbuildcompetition.game.Options;
import com.cssbham.cssbuildcompetition.game.command.CommandHandler;
import com.cssbham.cssbuildcompetition.game.command.CommandRouter;
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
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class BuildPhase extends Phase implements CommandHandler {

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

        Component message = Component.newline()
                .append(Component.text("Time to build!", NamedTextColor.GREEN, TextDecoration.BOLD))
                .append(Component.newline())
                .append(Component.newline())
                .append(Component.text("You have ", NamedTextColor.GREEN))
                .append(Component.text(TimeFormat.convertToHoursMinutes(duration), NamedTextColor.WHITE))
                .append(Component.text(" to build according to the theme: ", NamedTextColor.GREEN))
                .append(Component.text(theme, NamedTextColor.WHITE))
                .append(Component.text(".", NamedTextColor.GREEN))
                .append(Component.newline())
                .append(Component.text("At any point you may use ", NamedTextColor.GRAY))
                .append(Component.text("/c home", NamedTextColor.WHITE))
                .append(Component.text(" to return to your plot.", NamedTextColor.GRAY))
                .append(Component.newline())
                .append(Component.newline())
                .append(Component.text("Good luck!", NamedTextColor.GREEN))
                .append(Component.newline());
        Sound sound = Sound.sound(Key.key("block.note_block.pling"),
                Sound.Source.NEUTRAL, 2, 2);
        Title title = Title.title(Component.text("Start building!", NamedTextColor.GREEN),
                Component.text("Theme: " + theme, NamedTextColor.GRAY),
                Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(5000), Duration.ofMillis(500)));

        for (Team team : teamManager.getTeams()) {
            Plot plot = plotArea.getPlot(team.getPlotId());
            if (plot == null) {
                super.logSevere("Assigned plot " + team.getPlotId().toString() + " for team " + team.getName() + " does not exist!");
                continue;
            }
            plot.setFlag(ServerPlotFlag.SERVER_PLOT_TRUE);
            updatePlotAccessRights(team);
            plot.getCenter((centre) -> {
                for (UUID uuid : team.getPlayers()) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null) {
                        player.teleport(PlotSquaredHelper.convertPlotSquaredLocationToBukkitLocation(centre));
                        player.showTitle(title);
                        player.playSound(sound, Sound.Emitter.self());
                        player.sendMessage(message);
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
            clearPlotAccessRights(plot);
        }
    }

    @Override
    public void registerCommands(@NotNull CommandRouter commandRouter) {
        commandRouter.registerCommand("home", this);
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

    @EventHandler
    public void onTeamChange(PlayerChangeTeamEvent event) {
        Team oldTeam = event.getOldTeam();
        Team newTeam = event.getNewTeam();
        if (oldTeam != null) {
            updatePlotAccessRights(oldTeam);
        }
        if (newTeam != null) {
            updatePlotAccessRights(newTeam);
        }
    }

    private void clearPlotAccessRights(Plot plot) {
        super.logInfo("Clearing plot " + plot.getId() + " access rights");
        for (UUID trusted : new HashSet<>(plot.getTrusted())) {
            plot.removeTrusted(trusted);
        }
    }

    private void updatePlotAccessRights(Team team) {
        super.logInfo("Updating plot " + team.getPlotId().toString() + " access rights for team " + team.getName());
        Plot plot = plotArea.getPlot(team.getPlotId());
        if (plot == null) {
            super.logSevere("Assigned plot " + team.getPlotId().toString() + " for team " + team.getName() + " does not exist!");
            return;
        }
        for (UUID trusted : new HashSet<>(plot.getTrusted())) {
            plot.removeTrusted(trusted);
        }
        for (UUID trusted : team.getPlayers()) {
            plot.addTrusted(trusted);
        }
    }

    @Override
    public boolean handle(Player player, String[] args) {
        Team team = teamManager.getTeamOfPlayer(player.getUniqueId());
        if (team == null) {
            player.sendMessage(Component.text("You are not in a team.", NamedTextColor.RED));
            return true;
        }

        Plot plot = plotArea.getPlot(team.getPlotId());
        plot.getCenter((centre) -> player.teleport(PlotSquaredHelper.convertPlotSquaredLocationToBukkitLocation(centre)));
        return true;
    }
}
