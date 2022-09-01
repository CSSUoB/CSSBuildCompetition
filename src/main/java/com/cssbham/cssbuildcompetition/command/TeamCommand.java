package com.cssbham.cssbuildcompetition.command;

import com.cssbham.cssbuildcompetition.BuildCompetitionPlugin;
import com.cssbham.cssbuildcompetition.event.PlayerChangeTeamEvent;
import com.cssbham.cssbuildcompetition.exception.PlayerNotInTeamException;
import com.cssbham.cssbuildcompetition.game.Competition;
import com.cssbham.cssbuildcompetition.game.team.Team;
import com.cssbham.cssbuildcompetition.util.CommandPreconditions;
import com.cssbham.cssbuildcompetition.util.TabHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class TeamCommand implements TabExecutor {

    private final BuildCompetitionPlugin plugin;

    public TeamCommand(BuildCompetitionPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (plugin.getCompetition() == null) {
            sender.sendMessage(Component.text("No competition is currently running.", NamedTextColor.RED));
            return true;
        }
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("You must be a player to use this command.", NamedTextColor.RED));
            return true;
        }


        if (args.length == 0) {
            if (plugin.getCompetition().getTeamManager().getPlayerRegistry().hasPlayer(player.getUniqueId())) {
                sender.sendMessage(Component.text("To view members in your team, type ", NamedTextColor.WHITE)
                        .append(Component.text("/team info", NamedTextColor.GREEN)));
                sender.sendMessage(Component.text("To leave your team, type ", NamedTextColor.WHITE)
                        .append(Component.text("/team leave", NamedTextColor.GREEN)));
            } else {
                sender.sendMessage(Component.text("To join a team, type ", NamedTextColor.WHITE)
                        .append(Component.text("/team join", NamedTextColor.GREEN)));
                sender.sendMessage(Component.text("To join another player, type ", NamedTextColor.WHITE)
                        .append(Component.text("/team join <player name>", NamedTextColor.GREEN)));
            }
            return true;
        }

        try {
            switch (args[0]) {
                case "info" -> this.handleInfo(args, player);
                case "join" -> this.handleJoin(args, player);
                case "leave" -> this.handleLeave(args, player);
            }
        } catch (PlayerNotInTeamException ignored) {
            sender.sendMessage(Component.text("You are not in a team.", NamedTextColor.RED));
        }

        return true;
    }

    public void handleInfo(String[] args, Player player) {
        CommandPreconditions.requireTeam(plugin, player);

        Team team = plugin.getCompetition().getTeamManager().getTeamOfPlayer(player.getUniqueId());

        player.sendMessage(Component.text("You are on ", NamedTextColor.GREEN)
                .append(Component.text(team.getName(), NamedTextColor.WHITE))
                .append(Component.text(":", NamedTextColor.GREEN)));
        for (UUID teammate : team.getPlayers()) {
            OfflinePlayer teammatePlayer = plugin.getServer().getOfflinePlayer(teammate);
            player.sendMessage(Component.text(" - ", NamedTextColor.WHITE)
                    .append(Component.text(
                            teammatePlayer.getName() == null
                                    ? teammate.toString()
                                    : teammatePlayer.getName(),
                            NamedTextColor.WHITE))
                    .append(Component.text(teammate == player.getUniqueId() ? " (You)" : "", NamedTextColor.GRAY)));
        }
    }

    public void handleJoin(String[] args, Player player) {
        Competition competition = plugin.getCompetition();

        if (!competition.getTeamManager().isJoiningAllowed()) {
            player.sendMessage(Component.text("Joining teams is disallowed at this stage of the competition.", NamedTextColor.RED));
            return;
        }

        if (competition.isPlayerInCompetition(player.getUniqueId())) {
            player.sendMessage(Component.text("You are already in a team.", NamedTextColor.RED));
            return;
        }

        if (args.length < 2) {
            Team team = competition.getTeamManager().addPlayerToAvailableTeam(player.getUniqueId());
            PlayerChangeTeamEvent.dispatchEvent(player.getUniqueId(), null, team);
        } else {
            String playerName = args[1];
            Player otherPlayer = plugin.getServer().getPlayer(playerName);
            if (otherPlayer == null) {
                player.sendMessage(Component.text("Player not found.", NamedTextColor.RED));
            } else {
                Team team = competition.getTeamManager().getTeamOfPlayer(otherPlayer.getUniqueId());
                if (team == null) {
                    player.sendMessage(Component.text(otherPlayer.getName() + " is not in a team.", NamedTextColor.RED));
                } else if (team.getPlayers().size() >= team.getLimit()) {
                    player.sendMessage(Component.text(team.getName() + " is full.", NamedTextColor.RED));
                } else {
                    team.addPlayer(player.getUniqueId());
                    PlayerChangeTeamEvent.dispatchEvent(player.getUniqueId(), null, team);
                }
            }
        }
    }

    public void handleLeave(String[] args, Player player) {
        CommandPreconditions.requireTeam(plugin, player);

        Competition competition = plugin.getCompetition();

        if (!competition.getTeamManager().isLeavingAllowed()) {
            player.sendMessage(Component.text("Leaving teams is disallowed at this stage of the competition.", NamedTextColor.RED));
            return;
        }

        Team team = competition.getTeamManager().getTeamOfPlayer(player.getUniqueId());
        team.removePlayer(player.getUniqueId());
        PlayerChangeTeamEvent.dispatchEvent(player.getUniqueId(), team, null);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Competition competition = plugin.getCompetition();
        if (competition == null) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            return TabHelper.matchTabComplete(args[0], List.of("join", "info", "leave"));
        } else if (args.length == 2) {
            if (args[0].equals("join")) {
                return null;
            }
        }
        return Collections.emptyList();
    }
}
