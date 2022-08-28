package com.cssbham.cssbuildcompetition.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class PlotSquaredHelper {

    // why
    public static Location convertPlotSquaredLocationToBukkitLocation(com.plotsquared.core.location.Location location) {
        //TODO check world exists
        return new Location(Bukkit.getWorld(location.getWorldName()), location.getX(), location.getY(), location.getZ());
    }

}
