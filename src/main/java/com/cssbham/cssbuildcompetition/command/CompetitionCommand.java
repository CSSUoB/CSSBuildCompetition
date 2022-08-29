package com.cssbham.cssbuildcompetition.command;

import com.cssbham.cssbuildcompetition.BuildCompetitionPlugin;
import com.cssbham.cssbuildcompetition.exception.PlayerNotInTeamException;
import com.cssbham.cssbuildcompetition.util.TabHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CompetitionCommand implements TabExecutor {

    private final BuildCompetitionPlugin plugin;

    public CompetitionCommand(BuildCompetitionPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (plugin.getCompetition() == null) {
            sender.sendMessage(Component.text("No competition commands are available right now.", NamedTextColor.RED));
            return true;
        }
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("You must be a player to use this command.", NamedTextColor.RED));
            return true;
        }
        if (args.length == 0 || !plugin.getCompetition().getCommandRouter().handle(args[0], player, Arrays.copyOfRange(args, 1, args.length))) {
            showHelp(player);
        }
        return true;
    }

    private void showHelp(Player player) {
        if (plugin.getCompetition().getCommandRouter().getCommands().isEmpty()) {
            player.sendMessage(Component.text("No competition commands are available right now.", NamedTextColor.RED));
            return;
        }

        player.sendMessage(Component.text("Available commands:"));
        for (String command : plugin.getCompetition().getCommandRouter().getCommands().keySet()) {
            player.sendMessage(Component.text("/competition ", NamedTextColor.GRAY)
                    .append(Component.text(command, NamedTextColor.WHITE)));
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (plugin.getCompetition() == null || !(sender instanceof Player player)) {
            return Collections.emptyList();
        }
        if (args.length == 1) {
            TabHelper.matchTabComplete(args[0], plugin.getCompetition().getCommandRouter().getCommands().keySet().stream().toList());
        }
        //TODO command tab delegation
        return Collections.emptyList();
    }
}
