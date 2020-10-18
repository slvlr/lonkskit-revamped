package me.aiglez.lonkskit.abilities.helpers;

import me.aiglez.lonkskit.WorldProvider;
import me.aiglez.lonkskit.utils.Logger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

/*
 * Related with Builder ability
 */
public class ConstructHelper {

    private static final int WALL_TALL = 2;
    private static final int WALL_WIDE = 5;
    private static final Material WALL_MATERIAL = Material.STONE;


    public static boolean buildWallAt(Location location) {
        Block start = location.getBlock();
        Block end = WorldProvider.KP_WORLD.getBlockAt(
                start.getX() + 1,
                start.getY() + 1,
                start.getZ() + 1
        );

        final BlockIterator it = getBlocksBetween(start.getLocation(), end.getLocation());

        int placed = 0;
        while (it.hasNext()) {
            final Block block = it.next();;
            block.setType(WALL_MATERIAL);

            placed++;
        }

        Logger.debug("[Construct] Placed {0} block(s)", placed);

        return true;
    }

    private static boolean canBuildAt(Location location) {
        return location.getBlock().isEmpty();
    }

    public static BlockIterator getBlocksBetween(Location start, Location end) {
        final Vector direction = end.toVector().subtract(start.toVector());

        return new BlockIterator(WorldProvider.KP_WORLD, start.toVector(), direction, 0, 50);
    }
}
