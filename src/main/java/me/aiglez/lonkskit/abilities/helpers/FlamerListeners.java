package me.aiglez.lonkskit.abilities.helpers;

import me.aiglez.lonkskit.KitPlugin;
import me.aiglez.lonkskit.abilities.Ability;
import me.aiglez.lonkskit.players.LocalPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

public class FlamerListeners implements Listener {

    public FlamerListeners(KitPlugin plugin) {
        plugin.registerListener(this);
    }

    @EventHandler
    private void onDamageEvent(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK || event.getCause() == EntityDamageEvent.DamageCause.FIRE || event.getCause() == EntityDamageEvent.DamageCause.LAVA || event.getCause() == EntityDamageEvent.DamageCause.HOT_FLOOR) {
                if (refresh(player)) {
                    event.setCancelled(true);
                    try {
                        Ability.get("flamer").applyEffects(LocalPlayer.get(player));
                    } catch (Exception ignored) { }
                }
            }

            if (event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK && event.getCause() == EntityDamageEvent.DamageCause.FIRE && event.getCause() == EntityDamageEvent.DamageCause.LAVA) {
                if (refresh(player)) {
                    event.setCancelled(true);
                    try {
                        Ability.get("flamer").applyEffects(LocalPlayer.get(player));
                    } catch (Exception ignored) { }
                }
            }

            if (event.getEntity().getLocation().getBlock().getType() == Material.MAGMA_BLOCK) {
                if (refresh(player)) {
                    event.setCancelled(true);
                    try {
                        Ability.get("flamer").applyEffects(LocalPlayer.get(player));
                    } catch (Exception ignored) { }
                }
            }
        }
    }

    private boolean refresh(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && null != item.getItemMeta() && item.getItemMeta().getDisplayName() != null) {
                if (item.getItemMeta().getDisplayName().contains("Molten Blade")) {
                    return true;
                }
            }
        }
        return false;
    }
}
