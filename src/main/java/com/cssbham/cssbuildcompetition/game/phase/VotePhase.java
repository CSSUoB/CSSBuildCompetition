package com.cssbham.cssbuildcompetition.game.phase;

import com.cssbham.cssbuildcompetition.game.Options;
import com.cssbham.cssbuildcompetition.game.team.Team;
import com.cssbham.cssbuildcompetition.game.team.TeamManager;
import com.cssbham.cssbuildcompetition.util.MessageHelper;
import com.cssbham.cssbuildcompetition.util.TimeFormat;
import com.plotsquared.core.PlotSquared;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.PlotArea;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

public final class VotePhase extends Phase {

    private final TeamManager teamManager;
    private final PlotArea plotArea;
    private final long durationPerVote;

    private final Map<UUID, Integer> castVotes;
    private final Map<UUID, BossBar> bossbars;
    private final Queue<Team> upcomingTeams;
    private Team currentTeam;
    private long votingEndTime;
    private long delayEndTime;

    public VotePhase(TeamManager teamManager, Options options) {
        super("vote");

        this.teamManager = teamManager;
        this.plotArea = PlotSquared.get().getPlotAreaManager().getPlotArea(options.getPlotworld(), null);
        this.durationPerVote = TimeUnit.MILLISECONDS.convert(options.getVoteTime(), TimeUnit.SECONDS);

        this.castVotes = new HashMap<>();
        this.bossbars = new HashMap<>();

        upcomingTeams = new LinkedList<>();
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!teamManager.getPlayerRegistry().getPlayers().contains(player.getUniqueId())) {
            return;
        }
        event.setCancelled(true);

        if (event.getHand() != EquipmentSlot.HAND || event.getItem() == null) {
            return;
        }

        if (currentTeam != null && currentTeam.getPlayers().contains(player.getUniqueId())) {
            Component message = Component.text("You may not vote for your own team.", NamedTextColor.RED);
            return;
        }

        ItemStack item = event.getItem();

        Sound sound = Sound.sound(Key.key("entity.experience_orb.pickup"),
                Sound.Source.NEUTRAL, 2, 1);
        for (VoteItem voteItem : VoteItem.values()) {
            if (voteItem.getItem().isSimilar(item)) {
                Component message = Component.text("You voted ", NamedTextColor.GREEN)
                        .append(Component.text(voteItem.getName(), voteItem.getTextColor()))
                        .append(Component.text("!", NamedTextColor.GREEN));

                castVotes.put(player.getUniqueId(), voteItem.getScore());
                BossBar bar = bossbars.get(player.getUniqueId());
                bar.name(Component.text("Your vote: ", NamedTextColor.WHITE)
                        .append(Component.text(voteItem.getName(), voteItem.getTextColor())));
                bar.color(voteItem.getBossbarBarColor());
                bar.overlay(BossBar.Overlay.PROGRESS);

                player.playSound(sound, Sound.Emitter.self());
                player.sendMessage(message);
//                bar.progress(1);
                break;
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (!teamManager.getPlayerRegistry().getPlayers().contains(event.getPlayer().getUniqueId())) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        if (!teamManager.getPlayerRegistry().getPlayers().contains(event.getPlayer().getUniqueId())) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!teamManager.getPlayerRegistry().getPlayers().contains(event.getWhoClicked().getUniqueId())) {
            return;
        }
        event.setCancelled(true);
    }

    @Override
    public void start() {
        upcomingTeams.clear();
        bossbars.clear();
        castVotes.clear();
        currentTeam = null;
        upcomingTeams.addAll(teamManager.getTeams());

        Component message = Component.newline()
                .append(Component.text("Time to vote!", NamedTextColor.GREEN, TextDecoration.BOLD))
                .append(Component.newline())
                .append(Component.newline())
                .append(Component.text("You have ", NamedTextColor.GREEN))
                .append(Component.text(TimeFormat.convertToHumanReadableTime(durationPerVote), NamedTextColor.WHITE))
                .append(Component.text(" per team to vote from ", NamedTextColor.GREEN))
                .append(Component.text("1 to 5", NamedTextColor.WHITE))
                .append(Component.text(" by right-clicking the option in your hand.", NamedTextColor.GREEN))
//                .append(Component.newline())
//                .append(Component.text("Please try to vote fairly for each team to keep the competition fun.", NamedTextColor.GRAY))
                .append(Component.newline())
                .append(Component.newline())
                .append(Component.text("Good luck!", NamedTextColor.GREEN))
                .append(Component.newline());
        for (UUID uuid : teamManager.getPlayerRegistry().getPlayers()) {
            bossbars.put(uuid, BossBar.bossBar(Component.text("XX remaining"),
                    1, BossBar.Color.PURPLE, BossBar.Overlay.NOTCHED_10));

            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.sendMessage(message);
            }
        }

        this.delayEndTime = System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(8, TimeUnit.SECONDS);
    }

    @Override
    public void end() {
        for (Map.Entry<UUID, BossBar> entry : bossbars.entrySet()) {
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player != null) {
                player.hideBossBar(entry.getValue());
            }
        }
    }

    @Override
    public boolean tick() {
        long delayRemaining = delayEndTime - System.currentTimeMillis();
        if (delayRemaining > 0) {
            return false;
        }

        long timeRemaining = votingEndTime - System.currentTimeMillis();
        if (timeRemaining <= 0) {
            if (currentTeam != null) {
                currentTeam.setScore(castVotes.values().stream().mapToInt(Integer::intValue).sum());
            }

            if (!advanceVote()) {
                return true;
            }
        }
        if (currentTeam != null) {
            long recalculatedTimeRemaining = votingEndTime - System.currentTimeMillis();
            Component voteTimeMessage = Component.text(TimeFormat.convertToHumanReadableTime(recalculatedTimeRemaining) + " remaining to vote!");
            Component currentTeamMessage = Component.text("Players are voting for your team!");

            for (UUID uuid : teamManager.getPlayerRegistry().getPlayers()) {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    BossBar bossbar = bossbars.get(uuid);
                    bossbar.progress((float) recalculatedTimeRemaining / durationPerVote);
                    if (!castVotes.containsKey(uuid)) {
                        if (currentTeam.getPlayers().contains(uuid)) {
                            bossbar.name(currentTeamMessage);
                        } else {
                            bossbar.name(voteTimeMessage);
                        }
                        bossbar.color(BossBar.Color.BLUE);
                        bossbar.overlay(BossBar.Overlay.NOTCHED_10);
                    }
                    player.showBossBar(bossbar);
                }
            }
        }
        return false;
    }

    private boolean advanceVote() {
        castVotes.clear();
        Team next = upcomingTeams.poll();
        if (next == null) {
            return false;
        } else {
            currentTeam = next;
            votingEndTime = System.currentTimeMillis() + durationPerVote;
            teleportPlayersToVote(currentTeam);
            return true;
        }
    }

    private void teleportPlayersToVote(Team team) {
        Plot plot = plotArea.getPlot(team.getPlotId());
        plot.getCenter((centre) -> {
            Component message = Component.text("Vote for ", NamedTextColor.GREEN, TextDecoration.BOLD)
                    .append(MessageHelper.decorateTeamName(team, NamedTextColor.WHITE, TextDecoration.BOLD))
                    .append(Component.text("!", NamedTextColor.GREEN, TextDecoration.BOLD));
            Title title = Title.title(Component.text(team.getName(), NamedTextColor.WHITE),
                    Component.text(String.join(", ", team.getPlayersNames()), NamedTextColor.GRAY),
                    Title.Times.times(Duration.ZERO, Duration.ofMillis(2500), Duration.ofMillis(500)));
            Sound sound = Sound.sound(Key.key("block.note_block.pling"),
                    Sound.Source.NEUTRAL, 2, 1);

            for (UUID uuid : teamManager.getPlayerRegistry().getPlayers()) {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    // fuck you plotsquared (again)
                    player.teleport(new Location(Bukkit.getWorld(plot.getWorldName()), centre.getX(), centre.getY(), centre.getZ()));

                    player.getInventory().clear();
                    for (VoteItem item : VoteItem.values()) {
                        player.getInventory().setItem(item.getSlot(), item.getItem());
                    }

                    player.sendMessage(message);
                    player.showTitle(title);
                    player.playSound(sound, Sound.Emitter.self());
                }
            }
        });
    }

}

enum VoteItem {

    FIVE("Very Good", Material.PURPLE_STAINED_GLASS_PANE, 5, 4, NamedTextColor.DARK_PURPLE, BossBar.Color.PURPLE),
    FOUR("Good", Material.LIME_STAINED_GLASS_PANE, 4, 3, NamedTextColor.GREEN, BossBar.Color.GREEN),
    THREE("Okay", Material.YELLOW_STAINED_GLASS_PANE, 3, 2, NamedTextColor.YELLOW, BossBar.Color.YELLOW),
    TWO("Poor", Material.ORANGE_STAINED_GLASS_PANE, 2, 1, NamedTextColor.GOLD, BossBar.Color.YELLOW),
    ONE("Very Poor", Material.RED_STAINED_GLASS_PANE, 1, 0, NamedTextColor.DARK_RED, BossBar.Color.RED);

    private String name;
    private final ItemStack item;
    private final int score;
    private final int slot;
    private final NamedTextColor textColor;
    private final BossBar.Color bossbarBarColor;

    VoteItem(String name, Material type, int score, int slot, NamedTextColor textColor, BossBar.Color bossbarBarColor) {
        this.name = name;
        this.score = score;
        this.slot = slot;
        this.textColor = textColor;
        this.bossbarBarColor = bossbarBarColor;

        ItemStack itemStack = new ItemStack(type);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(LegacyComponentSerializer.legacySection().serialize(
                Component.text()
                        .append(Component.text(name, textColor, TextDecoration.BOLD))
                        .append(Component.text(" (+" + score + " point(s))", NamedTextColor.GRAY))
                        .build()));
        itemStack.setItemMeta(itemMeta);
        this.item = itemStack;
    }

    public ItemStack getItem() {
        return item;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public int getSlot() {
        return slot;
    }

    public NamedTextColor getTextColor() {
        return textColor;
    }

    public BossBar.Color getBossbarBarColor() {
        return bossbarBarColor;
    }
}
