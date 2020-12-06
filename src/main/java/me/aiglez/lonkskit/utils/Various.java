package me.aiglez.lonkskit.utils;

import com.google.common.base.Preconditions;
import me.aiglez.lonkskit.Constants;
import me.aiglez.lonkskit.WorldProvider;
import me.aiglez.lonkskit.messages.Replaceable;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.lucko.helper.text3.Text;
import me.lucko.helper.utils.Players;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class Various {

    public static void broadcast(String message, Object... replacements) {
        Preconditions.checkNotNull(message, "message may not be null");
        Players.stream()
                .filter(player -> player.getWorld().getUID().equals(WorldProvider.KP_WORLD.getUID()))
                .forEach(player -> player.sendMessage(Text.colorize(Replaceable.handle(message, replacements))));
    }

    public static boolean isThrowable(ItemStack item) {
        Preconditions.checkNotNull(item, "item may not be null");
        if(!item.hasItemMeta() || !item.getItemMeta().hasLore()) return false;
        return item.getLore().stream().anyMatch(s -> StringUtils.containsIgnoreCase(s, Constants.THROWABLE_LORE));
    }

    public static void damage(LocalPlayer localPlayer, double amount, boolean ignoreArmor) {
        Preconditions.checkNotNull(localPlayer, "local player may not be null");
        if(ignoreArmor) {
            localPlayer.toBukkit().setHealth(Math.min(localPlayer.toBukkit().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() - amount, 20));
        } else {
            localPlayer.toBukkit().damage(amount);
        }
    }

    public static boolean assertNotSurroundedWithCactus(Block block) {
        Preconditions.checkNotNull(block, "block may not be null");
        return block.getRelative(BlockFace.UP).getType() != Material.CACTUS &&
                block.getRelative(BlockFace.DOWN).getType() != Material.CACTUS &&
                block.getRelative(BlockFace.EAST).getType() != Material.CACTUS &&
                block.getRelative(BlockFace.WEST).getType() != Material.CACTUS;
    }

    public static Vector makeFinite(Vector original) {
        Preconditions.checkNotNull(original, "original vector may not be null");
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
