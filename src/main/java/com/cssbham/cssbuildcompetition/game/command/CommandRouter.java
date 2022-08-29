package com.cssbham.cssbuildcompetition.game.command;

import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CommandRouter {

    private final Map<String, CommandHandler> commands;

    public CommandRouter() {
        commands = new HashMap<>();
    }

    public void registerCommand(String command, CommandHandler handler) {
        commands.put(command, handler);
    }

    public boolean handle(String command, Player player, String[] args) {
        if (commands.containsKey(command)) {
            return commands.get(command).handle(player, args);
        }
        return false;
    }

    public Map<String, CommandHandler> getCommands() {
        return Collections.unmodifiableMap(commands);
    }

    public void unregisterCommands() {
        commands.clear();
    }

}
