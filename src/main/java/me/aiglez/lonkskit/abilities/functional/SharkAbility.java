package me.aiglez.lonkskit.abilities.functional;

import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.FunctionalAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.lucko.helper.Events;
import me.lucko.helper.config.yaml.YAMLConfigurationLoader;
import me.lucko.helper.event.filter.EventFilters;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.io.IOException;

/**
 * @author AigleZ
 * @date 04/10/2020
 */

public class SharkAbility extends FunctionalAbility {

    public SharkAbility(YAMLConfigurationLoader configurationLoader) throws IOException {
        super("shark", configurationLoader);
    }

    @Override
    public void registerListeners() {
        Events.subscribe(PlayerMoveEvent.class)
                .filter(EventFilters.ignoreCancelled())
                .filter(EventFilters.ignoreSameBlock())
                .filter(AbilityPredicates.hasAbility(this))
                .handler(e -> {
                    final LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());
                    if(isWater(localPlayer.getLocation()) || isWater(localPlayer.toBukkit().getEyeLocation())) {
                        applyEffects(localPlayer);
                    }
                });

        Events.subscribe(EntityDamageEvent.class)
                .filter(e -> e.getEntity() instanceof Player)
                .filter(e -> e.getCause() == EntityDamageEvent.DamageCause.DROWNING)
                .filter(AbilityPredicates.possiblyHasAbility(this))
                .handler(e -> {
                    e.setDamage(0);
                    e.setCancelled(true);
                });
    }

    private boolean isWater(Location location) {
        return location.getBlock().getType() == Material.WATER;
    }
}
