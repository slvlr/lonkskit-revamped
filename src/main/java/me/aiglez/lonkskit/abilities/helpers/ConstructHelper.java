package me.aiglez.lonkskit.abilities.helpers;

import me.aiglez.lonkskit.WorldProvider;
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
                start.getX() + WALL_WIDE,
                start.getY() + WALL_TALL,
                start.getZ()
        );

        final BlockIterator it = getBlocksBetween(start.getLocation(), end.getLocation());
        while (it.hasNext()) {
            final Block block = it.next();;

            block.setType(WALL_MATERIAL);
        }

        return true;
    }

    private static boolean canBuildAt(Location location) {
        return location.getBlock().isEmpty();
    }

    public static BlockIterator getBlocksBetween(Location start, Location end) {
        final Vector v = start.toVector().subtract(end.toVector());

        return new BlockIterator(WorldProvider.KP_WORLD, end.toVector(), v, 0, 0);
    }
}
