package me.aiglez.lonkskit.abilities.helpers;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/*
 * Related with Builder ability
 */
public class ConstructHelper {

    private static final int WALL_TALL = 2;
    private static final int WALL_WIDE = 5;
    private static final Material WALL_MATERIAL = Material.BRICK;


    public static boolean buildWallAt(Location location) {
        final Block base = location.getBlock().getRelative(BlockFace.UP);

        for (int x = 0; x < WALL_WIDE; x++) {
            base.getRelative(BlockFace.WEST, x).setType(WALL_MATERIAL);
            base.getRelative(BlockFace.EAST, x).setType(WALL_MATERIAL);

            for (int y = 0; y < WALL_TALL; y++) {
                base.getRelative(BlockFace.UP, y).setType(WALL_MATERIAL);
            }
        }
        return true;
    }

    private static boolean canBuildAt(Location location) {
        return location.getBlock().isEmpty();
    }

}
