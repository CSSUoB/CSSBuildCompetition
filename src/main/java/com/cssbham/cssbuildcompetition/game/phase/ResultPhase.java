package com.cssbham.cssbuildcompetition.game.phase;

import com.cssbham.cssbuildcompetition.BuildCompetitionPlugin;
import com.cssbham.cssbuildcompetition.game.Options;
import com.cssbham.cssbuildcompetition.game.team.Team;
import com.cssbham.cssbuildcompetition.game.team.TeamManager;
import com.cssbham.cssbuildcompetition.util.MessageHelper;
import com.cssbham.cssbuildcompetition.util.PlotSquaredHelper;
import com.plotsquared.core.PlotSquared;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.PlotArea;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class ResultPhase extends Phase {

    private final TeamManager teamManager;
    private final PlotArea plotArea;

    private final List<Team> scoreboard;

    private long endTime;

    public ResultPhase(TeamManager teamManager, Options options) {
        super("results");

        this.scoreboard = new ArrayList<>();
        this.teamManager = teamManager;
        this.plotArea = PlotSquared.get().getPlotAreaManager().getPlotArea(options.getPlotworld(), null);
    }

    @Override
    public void start() {
        this.endTime = System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(7, TimeUnit.SECONDS);
        scoreboard.addAll(teamManager.getTeams());
        scoreboard.sort((team1, team2) -> Integer.compare(team2.getScore(), team1.getScore()));
        if (scoreboard.size() == 0) {
            return;
        }
        Team winner = scoreboard.get(0);

        Plot plot = plotArea.getPlot(winner.getPlotId());
        Component message = Component.newline()
                .append(MessageHelper.decorateTeamName(winner, NamedTextColor.WHITE, TextDecoration.BOLD))
                .append(Component.text(" has won the game with ", NamedTextColor.GREEN, TextDecoration.BOLD))
                .append(Component.text(winner.getScore(), NamedTextColor.WHITE, TextDecoration.BOLD))
                .append(Component.text(" points! Congratulations!", NamedTextColor.GREEN, TextDecoration.BOLD))
                .append(Component.newline());
        Sound sound = Sound.sound(Key.key("ui.toast.challenge_complete"),
                Sound.Source.NEUTRAL, 2, 1);
        Title title = Title.title(Component.text(winner.getName() + " win!", NamedTextColor.WHITE),
                Component.text(String.join(", ", winner.getPlayersNames()), NamedTextColor.GRAY),
                Title.Times.times(Duration.ZERO, Duration.ofMillis(3000), Duration.ofMillis(500)));

        plot.getCenter((centre) -> {
            for (UUID uuid : teamManager.getPlayerRegistry().getPlayers()) {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    player.teleport(PlotSquaredHelper.convertPlotSquaredLocationToBukkitLocation(centre));

                    player.getInventory().clear();
                    player.sendMessage(message);
                    player.playSound(sound, Sound.Emitter.self());
                    player.showTitle(title);
                }
            }
        });

    }

    @Override
    public void end() {
        Component message = MessageHelper.decorateScoreboard(scoreboard);

        Sound sound = Sound.sound(Key.key("block.note_block.chime"),
                Sound.Source.NEUTRAL, 2, 0.5f);
        for (UUID uuid : teamManager.getPlayerRegistry().getPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.sendMessage(message);
                player.playSound(sound, Sound.Emitter.self());
            }
        }
        Bukkit.getConsoleSender().sendMessage(message);
    }

    @Override
    public boolean tick() {
        return System.currentTimeMillis() > endTime;
    }

}
