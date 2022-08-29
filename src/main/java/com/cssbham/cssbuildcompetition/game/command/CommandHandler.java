package com.cssbham.cssbuildcompetition.game.command;

import org.bukkit.entity.Player;

public interface CommandHandler {

    boolean handle(Player player, String[] args);

}
