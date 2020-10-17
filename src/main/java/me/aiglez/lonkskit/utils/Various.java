package me.aiglez.lonkskit.utils;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public class Various {

    public static boolean notSurroundedByCactus(Block block) {
        return block.getRelative(BlockFace.UP).getType() != Material.CACTUS;
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
