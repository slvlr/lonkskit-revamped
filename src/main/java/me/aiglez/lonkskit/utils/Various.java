package me.aiglez.lonkskit.utils;

import me.aiglez.lonkskit.players.LocalPlayer;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public class Various {

    public static void damage(LocalPlayer localPlayer, double amount, boolean ignoreArmor) {
        if(ignoreArmor) {
            localPlayer.toBukkit().setHealth(Math.min(localPlayer.toBukkit().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() - amount, 0));
        } else {
            localPlayer.toBukkit().damage(amount);
        }
    }

    public static boolean assertNotSurroundedWithCactus(Block block) {
        return block.getRelative(BlockFace.UP).getType() != Material.CACTUS &&
                block.getRelative(BlockFace.DOWN).getType() != Material.CACTUS &&
                block.getRelative(BlockFace.EAST).getType() != Material.CACTUS &&
                block.getRelative(BlockFace.WEST).getType() != Material.CACTUS;
    }

    public static Vector makeFinite(Vector original) {
        if(original == null) return null;

        double ox = original.getX(), fx = makeFinite(ox);
        double oy = original.getY(), fy = makeFinite(oy);
        double oz = original.getZ(), fz = makeFinite(oz);
        return new Vector(fx, fy, fz);
    }

    private static double makeFinite(double original) {
        if(Double.isNaN(original)) return 0.0D;
        if(Double.isInfinite(original)) return (original < 0.0D ? -1.0D : 1.0D);
        return original;
    }


}
