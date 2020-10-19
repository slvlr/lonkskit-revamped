package me.aiglez.lonkskit.abilities.helpers;

import me.aiglez.lonkskit.WorldProvider;
import me.aiglez.lonkskit.utils.Various;
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


    public static boolean buildWallAt(float yaw, Location location) {
        final Block base = location.getBlock();

        int minX, minY, minZ;
        int maxX, maxY, maxZ;

        minY = base.getY();
        maxY = minY + WALL_TALL -1;

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

        final List<Block> build = new ArrayList<>();
        boolean pass = true;
        for(int x = minX; x <= maxX; x++)
            for(int y = minY; y <= maxY; y++)
                for(int z = minZ; z <= maxZ; z++) {
                    final Block block = WorldProvider.KP_WORLD.getBlockAt(x, y, z);
                    if(canBuildAt(location)) {
                        build.add(block);
                    } else {
                        pass = false;
                    }
                }
        if(pass == false) {
            return false;
        }

        build.forEach(block -> block.setType(WALL_MATERIAL));
        return true;
    }

    private static boolean canBuildAt(Location location) {
        return location.getBlock().isEmpty() && Various.assertNotSurroundedWithCactus(location.getBlock());
    }
}
