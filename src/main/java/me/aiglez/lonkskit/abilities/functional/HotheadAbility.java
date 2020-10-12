package me.aiglez.lonkskit.abilities.functional;

import me.aiglez.lonkskit.WorldProvider;
import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.FunctionalAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.lucko.helper.Events;
import me.lucko.helper.config.ConfigurationNode;
import me.lucko.helper.scheduler.Ticks;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author AigleZ
 * @date 11/10/2020
 */
public class HotheadAbility extends FunctionalAbility {

    public HotheadAbility( ConfigurationNode configuration) {
        super("hothead", configuration);
    }

    @Override
    public void handleListeners() {
        Events.subscribe(PlayerToggleSneakEvent.class)
                .filter(AbilityPredicates.playerHasAbility(this))
                .handler(e -> {
                    final LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());

                    localPlayer.toBukkit().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, (int) Ticks.from(2, TimeUnit.SECONDS), 1));

                    final AtomicInteger burned = new AtomicInteger(0);
                    WorldProvider.KP_WORLD.getNearbyEntities(localPlayer.getLocation(), 4, 4, 4)
                            .forEach(entity -> {
                                if(entity.getUniqueId().equals(localPlayer.getUniqueId())) {
                                    return;
                                }
                                entity.setFireTicks(40);
                                burned.incrementAndGet();
                            });

                    localPlayer.msg("&c(Hothead - Debug) Burning {0} entities", burned.intValue());
                });

    }
}
