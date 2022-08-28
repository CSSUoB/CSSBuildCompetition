package com.cssbham.cssbuildcompetition.util;

import com.cssbham.cssbuildcompetition.game.team.Team;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.List;

public class MessageHelper {

    /**
     * Creates a new {@link Component} of the team name with a hover component
     * listing all players.
     *
     * @param team the team
     * @param textColor the colour of the text
     * @param decorations the decorations to apply to the text
     * @return the component
     */
    public static Component decorateTeamName(Team team, TextColor textColor, TextDecoration... decorations) {
        Component members = Component.text("Members in ", NamedTextColor.GREEN)
                .append(Component.text(team.getName(), NamedTextColor.WHITE))
                .append(Component.text(":", NamedTextColor.GREEN));
        for (String teammate : team.getPlayersNames()) {
            members = members.append(Component.newline())
                    .append(Component.text(" - ", NamedTextColor.GRAY))
                    .append(Component.text(teammate, NamedTextColor.WHITE));
        }
        return Component.text(team.getName(), textColor, decorations).hoverEvent(HoverEvent.showText(members));
    }

    /**
     * Creates a new {@link Component} of all teams with their scores in order
     *
     * @param sortedTeams the teams in order of score
     * @return the component
     */
    public static Component decorateScoreboard(List<Team> sortedTeams) {
        Component message = Component.text("Full scoreboard:", NamedTextColor.GREEN);
        for (Team team : sortedTeams) {
            message = message.append(Component.newline())
                    .append(Component.text(" - ", NamedTextColor.GRAY))
                    .append(MessageHelper.decorateTeamName(team, NamedTextColor.WHITE))
                    .append(Component.text(" with ", NamedTextColor.GRAY))
                    .append(Component.text(team.getScore() + " points", NamedTextColor.WHITE));

        }
        return message;
    }

}
