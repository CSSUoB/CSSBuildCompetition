package com.cssbham.cssbuildcompetition.game;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

//TODO non lazy method
public class Options implements ConfigurationSerializable {

    private String plotworld = "plotworld";
    private String theme = "TeX";
    private int buildTime = 600;
    private int maxTeamSize = 2;
    private int voteTime = 30;

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new java.util.HashMap<>();
        map.put("plotworld", plotworld);
        map.put("theme", theme);
        map.put("buildTime", buildTime);
        map.put("maxTeamSize", maxTeamSize);
        map.put("voteTime", voteTime);
        return map;
    }

    public static Options deserialize(@NotNull Map<String, Object> map) {
        Options options = new Options();
        options.plotworld = (String) map.get("plotworld");
        options.theme = (String) map.get("theme");
        options.buildTime = (int) map.get("buildTime");
        options.maxTeamSize = (int) map.get("maxTeamSize");
        options.voteTime = (int) map.get("voteTime");
        return options;
    }

    public String getPlotworld() {
        return plotworld;
    }

    public String getTheme() {
        return theme;
    }

    public int getBuildTime() {
        return buildTime;
    }

    public int getVoteTime() {
        return voteTime;
    }

    public int getMaxTeamSize() {
        return maxTeamSize;
    }
}
