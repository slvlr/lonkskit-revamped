package me.aiglez.lonkskit.abilities.helpers;

import me.aiglez.lonkskit.WorldProvider;
import me.aiglez.lonkskit.utils.Logger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

/*
 * Related with Builder ability
 */
public class ConstructHelper {

    private static final int WALL_TALL = 2;
    private static final int WALL_WIDE = 5;
    private static final Material WALL_MATERIAL = Material.STONE;

    public static boolean buildWallAt(float yaw, Block base) {
        int minX, minY, minZ;
        int maxX, maxY, maxZ;

        minY = base.getY();
        maxY = minY + WALL_TALL - 1;

        final int direction = Math.round(yaw / 90f);
        if(direction % 2 == 0) { // Direction = (0 || 2)
            minX = base.getX() - ((WALL_WIDE - 1) / 2);
            maxX = minX + WALL_WIDE - 1;
            maxZ = minZ = base.getZ();
        } else { // Direction = (1 || 3)
            minZ = base.getZ() - ((WALL_WIDE - 1) / 2);
            maxZ = minZ + WALL_WIDE - 1;
            maxX = minX = base.getX();
        }

        final List<Block> blocks = new ArrayList<>();
        int blockCount = 0;
        for(int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    final Block block = WorldProvider.KP_WORLD.getBlockAt(x, y, z);
                    blockCount++;
                    if (canBuildAt(block.getLocation())) {
                        blocks.add(block);
                    } else {
                        Logger.debug("[WallBuilder] You can't build there found block type {0}", block.getType());
                    }
                }
            }
        }
        Logger.debug("[WallBuilder] Block Count: {0}  ||  Buildable Blocks Count {1}", blockCount, blocks.size());
        if(blockCount == blocks.size()) {
            blocks.forEach(block -> block.setType(WALL_MATERIAL));
            return true;
        } else {
            return false;
        }
    }

    private static boolean canBuildAt(Location location) {
        return location.getBlock().isEmpty();
    }
}
