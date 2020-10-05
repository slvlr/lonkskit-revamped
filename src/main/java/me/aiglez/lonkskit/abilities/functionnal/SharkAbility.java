package me.aiglez.lonkskit.abilities.functionnal;

import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.FunctionalAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.lucko.helper.Events;
import me.lucko.helper.config.ConfigurationNode;
import me.lucko.helper.event.filter.EventFilters;
import me.lucko.helper.scheduler.Ticks;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.TimeUnit;

/**
 * @author AigleZ
 * @date 04/10/2020
 */
public class SharkAbility extends FunctionalAbility {

    public SharkAbility(ConfigurationNode configuration) {
        super("shark", configuration);
    }

    @Override
    public void handleListeners() {
        Events.subscribe(PlayerMoveEvent.class)
                .filter(EventFilters.ignoreCancelled())
                .filter(EventFilters.ignoreSameBlock())
                .filter(AbilityPredicates.playerHasAbility(this))
                .handler(e -> {
                    final LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());
                    if(isWater(localPlayer.getLocation()) || isWater(localPlayer.toBukkit().getEyeLocation())) {
                        localPlayer.toBukkit().addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, (int) Ticks.from(10, TimeUnit.SECONDS), 0));
                        localPlayer.toBukkit().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, (int) Ticks.from(10, TimeUnit.SECONDS), 1));

                        localPlayer.toBukkit().setRemainingAir((int) Ticks.from(10, TimeUnit.SECONDS));
                    } else {
                        localPlayer.toBukkit().removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                        localPlayer.toBukkit().removePotionEffect(PotionEffectType.SPEED);
                        localPlayer.toBukkit().setRemainingAir(localPlayer.toBukkit().getMaximumAir());
                    }
                });

        Events.subscribe(EntityDamageEvent.class)
                .filter(e -> e.getEntity() instanceof Player)
                .filter(e -> e.getCause() == EntityDamageEvent.DamageCause.DROWNING)
                .filter(AbilityPredicates.humanHasAbility(this))
                .handler(e -> {
                    e.setDamage(0);
                    e.setCancelled(true);
                });
    }

    private boolean isWater(Location location) {
        return location.getBlock().getType() == Material.WATER;
    }
}
