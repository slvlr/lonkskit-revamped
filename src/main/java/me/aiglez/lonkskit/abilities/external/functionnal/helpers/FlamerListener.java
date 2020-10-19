package me.aiglez.lonkskit.abilities.external.functionnal.helpers;

import me.aiglez.lonkskit.abilities.external.itembased.SonicAbility;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;
import java.io.IOException;
import java.util.Objects;

public class FlamerListener implements Listener {

    @EventHandler
    private void onDamageEvent(EntityDamageEvent event) {
        int duration = 1;
        int level = 1;
        try {
            level = SonicAbility.getConfig("flamer").getInt("level");
            duration = SonicAbility.getConfig("flamer").getInt("duration");
        } catch (IOException | InvalidConfigurationException e) {
            System.out.println("[LonksKits]  ERROR -> CANNOT ACCESS THE CONFIG FILE, CHECK FLAMER.YML!!");
        }
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK || event.getCause() == EntityDamageEvent.DamageCause.FIRE || event.getCause() == EntityDamageEvent.DamageCause.LAVA || event.getCause() == EntityDamageEvent.DamageCause.HOT_FLOOR) {
                if (refresh(player)) {
                    event.setCancelled(true);
                    assert PotionType.STRENGTH.getEffectType() != null;
                    player.addPotionEffect(new PotionEffect(PotionType.STRENGTH.getEffectType(), duration * 20, level));
                }

            }

            if (event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK && event.getCause() == EntityDamageEvent.DamageCause.FIRE && event.getCause() == EntityDamageEvent.DamageCause.LAVA) {
                if (refresh(player)) {
                    event.setCancelled(true);
                    assert PotionType.STRENGTH.getEffectType() != null;
                    player.addPotionEffect(new PotionEffect(PotionType.STRENGTH.getEffectType(), duration * 20, level));
                }
            }

            if (event.getEntity().getLocation().getBlock().getType() == Material.MAGMA_BLOCK) {
                if (refresh(player)) {
                    event.setCancelled(true);
                    assert PotionType.STRENGTH.getEffectType() != null;
                    player.addPotionEffect(new PotionEffect(PotionType.STRENGTH.getEffectType(), duration * 20, level));
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
