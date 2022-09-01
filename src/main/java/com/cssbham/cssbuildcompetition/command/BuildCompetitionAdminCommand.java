package com.cssbham.cssbuildcompetition.command;

import com.cssbham.cssbuildcompetition.BuildCompetitionPlugin;
import com.cssbham.cssbuildcompetition.event.PlayerChangeTeamEvent;
import com.cssbham.cssbuildcompetition.exception.CompetitionInProgressException;
import com.cssbham.cssbuildcompetition.exception.CompetitionNotRunningException;
import com.cssbham.cssbuildcompetition.game.Competition;
import com.cssbham.cssbuildcompetition.game.phase.Phase;
import com.cssbham.cssbuildcompetition.game.team.Team;
import com.cssbham.cssbuildcompetition.util.CommandPreconditions;
import com.cssbham.cssbuildcompetition.util.MessageHelper;
import com.cssbham.cssbuildcompetition.util.TabHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

//TODO add team swap command
public class BuildCompetitionAdminCommand implements TabExecutor {

    private final BuildCompetitionPlugin plugin;

    public BuildCompetitionAdminCommand(BuildCompetitionPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage("/bca info <phase|teams|score>");
            sender.sendMessage("/bca start");
            sender.sendMessage("/bca team <create|move|remove>");
            sender.sendMessage("/bca nextphase");
            return true;
        }

        try {
            if (args[0].equalsIgnoreCase("info")) {
                this.handleInfo(args, sender);

            } else if (args[0].equalsIgnoreCase("start")) {
                this.handleStart(args, sender);

            //TODO this
            } else if (args[0].equalsIgnoreCase("team")) {
                this.handleTeam(args, sender);

            } else {
                this.handleNextPhase(args, sender);

            }
        } catch (CompetitionInProgressException ignored) {
            sender.sendMessage(Component.text("A competition is already running.", NamedTextColor.RED));
        } catch (CompetitionNotRunningException ignored) {
            sender.sendMessage(Component.text("A competition is not running.", NamedTextColor.RED));
        }

        return true;
    }

    public void handleInfo(String[] args, CommandSender sender) {
        CommandPreconditions.requireCompetition(plugin);

        if (args.length < 2) {
            sender.sendMessage("/bca info <phase|teams|score>");
            return;
        }

        Competition competition = plugin.getCompetition();

        if (args[1].equalsIgnoreCase("phase")) {
            if (competition.getCurrentPhase() != null) {
                sender.sendMessage(Component.text("Current phase: ", NamedTextColor.GREEN)
                        .append(Component.text(competition.getCurrentPhase().getName(), NamedTextColor.WHITE)));
            }
            if (!competition.getUpcomingPhases().isEmpty()) {
                sender.sendMessage(Component.text("Upcoming phases: ", NamedTextColor.GREEN));
                for (Phase phase : competition.getUpcomingPhases()) {
                    sender.sendMessage(Component.text(" - ", NamedTextColor.GRAY)
                            .append(Component.text(phase.getName(), NamedTextColor.WHITE)));
                }
            }
        } else if (args[1].equalsIgnoreCase("score")) {
            List<Team> scoreboard = new ArrayList<>(competition.getTeamManager().getTeams());
            scoreboard.sort((team1, team2) -> Integer.compare(team2.getScore(), team1.getScore()));
            sender.sendMessage(MessageHelper.decorateScoreboard(scoreboard));
        } else if (args[1].equalsIgnoreCase("teams")) {
            for (Team team : competition.getTeamManager().getTeams()) {
                Component message = Component.text()
                        .append(MessageHelper.decorateTeamName(team, NamedTextColor.GREEN, TextDecoration.BOLD, TextDecoration.UNDERLINED))
                        .append(Component.newline())
                        .append(Component.text("Members: ", NamedTextColor.WHITE))
                        .append(Component.text(String.join(", ", team.getPlayersNames()), NamedTextColor.GRAY))
                        .append(Component.newline())
                        .append(Component.text("Plot ID: ", NamedTextColor.WHITE))
                        .append(Component.text(team.getPlotId().toString(), NamedTextColor.GRAY))
                        .append(Component.newline())
                        .append(Component.text("Score: ", NamedTextColor.WHITE))
                        .append(Component.text(team.getScore(), NamedTextColor.GRAY))
                        .build();

                sender.sendMessage(message);
            }
        }

        sender.sendMessage("/bca info <phase|teams|score>");
    }

    public void handleStart(String[] args, CommandSender sender) {
        CommandPreconditions.requireNoCompetition(plugin);

        try {
            plugin.startNewCompetition();
            sender.sendMessage(Component.text("A new competition has been started.", NamedTextColor.GREEN));
        } catch (Exception e) {
            sender.sendMessage(Component.text("An error occurred while starting the competition.", NamedTextColor.RED));
            e.printStackTrace();
        }
    }

    public void handleTeam(String[] args, CommandSender sender) {
        CommandPreconditions.requireCompetition(plugin);

        Competition competition = plugin.getCompetition();
        if (args.length < 2) {
            sender.sendMessage("/bca team <create|move|remove>");
            return;
        }
        if (args[1].equalsIgnoreCase("create")) {
            if (args.length < 3) {
                sender.sendMessage("/bca team create <player>");
                return;
            }
            String playerName = args[2];
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
            if (!offlinePlayer.hasPlayedBefore()) {
                sender.sendMessage(Component.text("Player not found.", NamedTextColor.RED));
                return;
            }
            if (competition.getTeamManager().isPlayerInCompetition(offlinePlayer.getUniqueId())) {
                sender.sendMessage(Component.text("Player is already in a team.", NamedTextColor.RED));
                return;
            }

            Team team = competition.getTeamManager().createNewTeam();
            team.addPlayer(offlinePlayer.getUniqueId());
            PlayerChangeTeamEvent.dispatchEvent(offlinePlayer.getUniqueId(), null, team);
            sender.sendMessage(Component.text(offlinePlayer.getName(), NamedTextColor.WHITE)
                    .append(Component.text(" has been added to ", NamedTextColor.GREEN))
                    .append(MessageHelper.decorateTeamName(team, NamedTextColor.WHITE)));

        } else if (args[1].equalsIgnoreCase("move")) {
            //TODO
            sender.sendMessage(Component.text("Not implemented yet.", NamedTextColor.RED));

        } else if (args[1].equalsIgnoreCase("remove")) {
            if (args.length < 3) {
                sender.sendMessage("/bca team remove <player>");
                return;
            }
            String playerName = args[2];
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
            if (!offlinePlayer.hasPlayedBefore()) {
                sender.sendMessage(Component.text("Player not found.", NamedTextColor.RED));
                return;
            }
            if (!competition.getTeamManager().isPlayerInCompetition(offlinePlayer.getUniqueId())) {
                sender.sendMessage(Component.text("Player is not in a team.", NamedTextColor.RED));
                return;
            }

            Team team = competition.getTeamManager().getTeamOfPlayer(offlinePlayer.getUniqueId());
            team.removePlayer(offlinePlayer.getUniqueId());
            PlayerChangeTeamEvent.dispatchEvent(offlinePlayer.getUniqueId(), team, null);
            sender.sendMessage(Component.text(offlinePlayer.getName(), NamedTextColor.WHITE)
                    .append(Component.text(" has been removed from ", NamedTextColor.GREEN))
                    .append(MessageHelper.decorateTeamName(team, NamedTextColor.WHITE)));
        }
    }

    public void handleNextPhase(String[] args, CommandSender sender) {
        CommandPreconditions.requireCompetition(plugin);

        Competition competition = plugin.getCompetition();

        if (args[0].equalsIgnoreCase("nextphase")) {
            Phase nextPhase = competition.advancePhase();
            if (nextPhase == null) {
                sender.sendMessage(Component.text("Ending final phase.", NamedTextColor.GREEN));
                return;
            }
            sender.sendMessage(Component.text("Advancing to phase ", NamedTextColor.GREEN)
                    .append(Component.text(nextPhase.getName(), NamedTextColor.WHITE))
                    .append(Component.text(".", NamedTextColor.GREEN)));
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return TabHelper.matchTabComplete(args[0], List.of("info", "start", "team", "nextphase"));
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("info")) {
                return TabHelper.matchTabComplete(args[1], List.of("phase", "teams", "score"));
            } else if (args[0].equalsIgnoreCase("team")) {
                return null;
            }
        }
        return Collections.emptyList();
    }
}
