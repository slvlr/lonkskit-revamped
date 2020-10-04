package me.aiglez.lonkskit.abilities.itembased;

import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.items.ItemStackBuilder;
import me.lucko.helper.config.ConfigurationNode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.TimeUnit;

/**
 * @author AigleZ
 * @date 03/10/2020 at 22:33
 */
public class BlinkAbility extends ItemStackAbility {

    private final ItemStack item;

    public BlinkAbility(ConfigurationNode configuration) {
        super("blink", configuration);
        this.item = ItemStackBuilder.of(Material.REDSTONE_TORCH)
                .name("&bBlinker")
                .build();
    }

    @Override
    public ItemStack getItemStack() { return this.item; }

    @Override
    public boolean isItemStack(ItemStack item) { return this.item.isSimilar(item); }

    // --------------------------------------------------------------------------------------------
    @Override
    public void whenUsed(PlayerInteractEvent e) {
        e.setCancelled(true);
        final LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());

        if(!cooldown.test(localPlayer)){
            localPlayer.msg("&9(Blink) &cPlease wait, {0} second(s) left", cooldown.remainingTime(localPlayer, TimeUnit.SECONDS));
            return;
        }

        //localPlayer.toBukkit().teleport(getTargetBlock(localPlayer).getLocation().clone().add(0D, 1D, 0D));
        int range = 30;
        Location from = localPlayer.toBukkit().getEyeLocation();
        Location to = localPlayer.toBukkit().getEyeLocation();
        while (to.distanceSquared(localPlayer.toBukkit().getEyeLocation()) <= range) {
            from = to;
            to = from.add(localPlayer.getLocation().getDirection().normalize().multiply(.5));
            if (isValid(to.getBlock().getType())) {
                localPlayer.toBukkit().teleport(from.clone().add(0, 1, 0));
                localPlayer.msg("&9(Blink) &aYou have blinked! (while loo)");
                return;
            }
        }
        localPlayer.toBukkit().teleport(to.add(0, 1, 0));
        localPlayer.msg("&9(Blink) &aYou have blinked!");
    }

    @Override
    public void handleListeners() {
    }

    private boolean isValid(Material material) {
        return !material.isAir() && material != Material.GRASS && material != Material.TALL_GRASS;
    }
}
