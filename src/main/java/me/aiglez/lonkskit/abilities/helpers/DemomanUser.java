package me.aiglez.lonkskit.abilities.helpers;

import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.Set;

public interface DemomanUser {

    Set<Location> getTraps();

    boolean hasTraps();

    void placeTrap(Block block);

    default int getTrapsCount() {
        return getTraps().size();
    }

}
