package me.aiglez.lonkskit.abilities.helpers;

import org.bukkit.Location;

import java.util.Set;

public interface DemomanUser {

    Set<Location> getTraps();

    boolean hasTraps();

    default int getTrapsCount() {
        return getTraps().size();
    }

}
