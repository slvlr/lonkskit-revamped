package me.aiglez.lonkskit.abilities.functional;

import me.aiglez.lonkskit.WorldProvider;
import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.FunctionalAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.lucko.helper.Events;
import me.lucko.helper.config.yaml.YAMLConfigurationLoader;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author AigleZ
 * @date 11/10/2020
 */
public class HotheadAbility extends FunctionalAbility {

    public HotheadAbility(YAMLConfigurationLoader configurationLoader) throws IOException {
        super("hothead", configurationLoader);
    }

    @Override
    public void registerListeners() {
        Events.subscribe(PlayerToggleSneakEvent.class)
                .filter(AbilityPredicates.hasAbility(this))
                .handler(e -> {
                    final LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());

                    applyEffects(localPlayer);

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
