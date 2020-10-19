package me.aiglez.lonkskit.abilities.functional.johan;

import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.FunctionalAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.lucko.helper.Events;
import me.lucko.helper.config.yaml.YAMLConfigurationLoader;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.io.IOException;

public class BerserkerAbility extends FunctionalAbility {

    public BerserkerAbility(YAMLConfigurationLoader configurationLoader) throws IOException {
        super("berserker", configurationLoader);
    }

    @Override
    public void registerListeners() {
        Events.subscribe(PlayerDeathEvent.class)
                .filter(e -> e.getEntity().getKiller() != null)
                .filter(AbilityPredicates.isKillerhaveAbility(this))
                .handler(e -> applyEffects(LocalPlayer.get(e.getEntity())));
    }
}
