package me.aiglez.lonkskit.abilities.helpers;

import me.aiglez.lonkskit.WorldProvider;
import me.aiglez.lonkskit.utils.Logger;
import me.lucko.helper.Schedulers;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/*
 * Related with Builder ability
 */
public class ConstructHelper {

    private static final int WALL_TALL = 2;
    private static final int WALL_WIDE = 5;
    private static final Material WALL_MATERIAL = Material.STONE;
    private static final Material DOME_MATERIAL = Material.GLASS;

    public static boolean buildWallAt(float yaw, Block base) {
        int minX, minY, minZ;
        int maxX, maxY, maxZ;

        minY = base.getY();
        maxY = minY + WALL_TALL + 1;

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
                    blocks.add(block);
//                    if (canBuildOnTop(block, null)) {
//
//                    } else {
//                        Logger.debug("[WallBuilder] You can't build there, found block type {0}", block.getType());
//                    }
                }
            }
        }

        Logger.debug("[WallBuilder] Block Count: {0}  ||  Buildable Blocks Count {1}", blockCount, blocks.size());
        if(blockCount == blocks.size()) {
            blocks.stream().filter(block -> canBuildOnTop(block,Material.AIR)).forEach(block -> block.setType(WALL_MATERIAL));
            Schedulers
                    .sync()
                    .runLater(() -> {
                        blocks.stream().filter(block -> canBuildOnTop(block,Material.AIR)).forEach(block -> {
                            block.setType(Material.AIR);
                            Schedulers.sync().runLater(() -> block.getState().update(),2L);
                        });
                    },200L);
            return true;
        } else {
            return false;
        }
    }

    public static boolean buildDome(Location location, long wait) {
        final int x = location.getBlockX();
        final int y = location.getBlockY();
        final int z = location.getBlockZ();

        //Bottom
        for(int i = x-1; i <= x+1; i++)
            for(int k = z-1; k <= z+1; k++) {
                final int j = y-1;
                final Block block = WorldProvider.KP_WORLD.getBlockAt(i, j, k);
                if(block.getType().equals(Material.AIR))
                    block.setType(DOME_MATERIAL);
            }
        //Top
        for(int i = x-1; i <= x+1; i++)
            for(int k = z-1; k <= z+1; k++) {
                final int j = y+3;
                final Block block = WorldProvider.KP_WORLD.getBlockAt(i, j, k);
                if(block.getType().equals(Material.AIR))
                    block.setType(DOME_MATERIAL);
            }
        //Side 1
        for(int i = x-1; i <= x+1; i++)
            for(int j = y; j <= y+2; j++) {
                final int k = z-2;
                final Block block = WorldProvider.KP_WORLD.getBlockAt(i, j, k);
                if(block.getType().equals(Material.AIR))
                    block.setType(DOME_MATERIAL);
            }
        //Side 3t
        for(int i = x-1; i <= x+1; i++)
            for(int j = y; j <= y+2; j++) {
                final int k = z+2;
                final Block block = WorldProvider.KP_WORLD.getBlockAt(i, j, k);
                if(block.getType().equals(Material.AIR))
                    block.setType(DOME_MATERIAL);
            }
        //Side 2
        for(int k = z-1; k <= z+1; k++)
            for(int j = y; j <= y+2; j++) {
                final int i = x-2;
                final Block block = WorldProvider.KP_WORLD.getBlockAt(i, j, k);
                if(block.getType().equals(Material.AIR))
                    block.setType(DOME_MATERIAL);
            }
        //Side 4
        for(int k = z-1; k <= z+1; k++)
            for(int j = y; j <= y+2; j++) {
                final int i = x+2;
                final Block block = WorldProvider.KP_WORLD.getBlockAt(i, j, k);
                if(block.getType().equals(Material.AIR))
                    block.setType(DOME_MATERIAL);
            }

        Schedulers.sync().runLater(() -> {
            //Bottom
            for(int i = x-1; i <= x+1; i++)
                for(int k = z-1; k <= z+1; k++) {
                    final int j = y-1;
                    final Block block = WorldProvider.KP_WORLD.getBlockAt(i, j, k);
                    if(block.getType() == DOME_MATERIAL)
                        block.setType(Material.AIR);
                    Schedulers.sync().runLater(() -> block.getState().update(),2L);
                }
            //Top
            for(int i = x-1; i <= x+1; i++)
                for(int k = z-1; k <= z+1; k++) {
                    final int j = y+3;
                    final Block block = WorldProvider.KP_WORLD.getBlockAt(i, j, k);
                    if(block.getType() == DOME_MATERIAL)
                        block.setType(Material.AIR);
                    Schedulers.sync().runLater(() -> block.getState().update(),2L);
                }
            //Side 1
            for(int i = x-1; i <= x+1; i++)
                for(int j = y; j <= y+2; j++) {
                    final int k = z-2;
                    final Block block = WorldProvider.KP_WORLD.getBlockAt(i, j, k);
                    if(block.getType() == DOME_MATERIAL)
                        block.setType(Material.AIR);
                    Schedulers.sync().runLater(() -> block.getState().update(),2L);
                }
            //Side 3
            for(int i = x-1; i <= x+1; i++)
                for(int j = y; j <= y+2; j++) {
                    final int k = z+2;
                    final Block block = WorldProvider.KP_WORLD.getBlockAt(i, j, k);
                    if(block.getType() == DOME_MATERIAL)
                        block.setType(Material.AIR);
                    Schedulers.sync().runLater(() -> block.getState().update(),2L);
                }
            //Side 2
            for(int k = z-1; k <= z+1; k++)
                for(int j = y; j <= y+2; j++) {
                    final int i = x-2;
                    final Block block = WorldProvider.KP_WORLD.getBlockAt(i, j, k);
                    if(block.getType() == DOME_MATERIAL)
                        block.setType(Material.AIR);
                    Schedulers.sync().runLater(() -> block.getState().update(),2L);
                }
            //Side 4
            for(int k = z-1; k <= z+1; k++)
                for(int j = y; j <= y+2; j++) {
                    final int i = x+2;
                    final Block block = WorldProvider.KP_WORLD.getBlockAt(i, j, k);
                    if(block.getType() == DOME_MATERIAL)
                        block.setType(Material.AIR);
                    Schedulers.sync().runLater(() -> block.getState().update(),2L);
                }
        }, wait);

        return true;
    }

    public static boolean canBuildDome(int x, int y, int z) {
        //Bottom
        for(int i = x-1; i <= x+1; i++)
            for(int k = z-1; k <= z+1; k++) {
                final int j = y-1;
                if(!canBuildOnTop(WorldProvider.KP_WORLD.getBlockAt(i, j, k), DOME_MATERIAL)) return false;
            }
        //Top
        for(int i = x-1; i <= x+1; i++)
            for(int k = z-1; k <= z+1; k++) {
                final int j = y+3;
                if(!canBuildOnTop(WorldProvider.KP_WORLD.getBlockAt(i, j, k), DOME_MATERIAL)) return false;
            }
        //Side 1
        for(int i = x-1; i <= x+1; i++)
            for(int j = y; j <= y+2; j++) {
                final int k = z-2;
                if(!canBuildOnTop(WorldProvider.KP_WORLD.getBlockAt(i, j, k), DOME_MATERIAL)) return false;
            }
        //Side 3
        for(int i = x-1; i <= x+1; i++)
            for(int j = y; j <= y+2; j++) {
                final int k = z+2;
                if(!canBuildOnTop(WorldProvider.KP_WORLD.getBlockAt(i, j, k), DOME_MATERIAL)) return false;
            }
        //Side 2
        for(int k = z-1; k <= z+1; k++)
            for(int j = y; j <= y+2; j++) {
                final int i = x-2;
                if(!canBuildOnTop(WorldProvider.KP_WORLD.getBlockAt(i, j, k), DOME_MATERIAL)) return false;
            }
        //Side 4
        for(int k = z-1; k <= z+1; k++)
            for(int j = y; j <= y+2; j++) {
                final int i = x+2;
                if(!canBuildOnTop(WorldProvider.KP_WORLD.getBlockAt(i, j, k), DOME_MATERIAL)) return false;
            }

        return true;
    }

    private static boolean canBuildOnTop(Block block, @Nullable Material avoid) {
        if(avoid != null && block.getType() == avoid) {
            return false;
        }
        return block.isEmpty();
    }
}
