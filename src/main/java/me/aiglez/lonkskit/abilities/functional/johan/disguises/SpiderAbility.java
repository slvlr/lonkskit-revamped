package me.aiglez.lonkskit.abilities.functional.johan.disguises;

import me.aiglez.lonkskit.abilities.FunctionalAbility;
import me.aiglez.lonkskit.abilities.functional.johan.DisguiseAbilities;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.lucko.helper.Events;
import me.lucko.helper.config.yaml.YAMLConfigurationLoader;
import org.bukkit.event.player.PlayerMoveEvent;

import java.io.IOException;

import static me.aiglez.lonkskit.abilities.functional.johan.DisguiseAbilities.nearWeb;

public class SpiderAbility extends FunctionalAbility {

    public SpiderAbility(YAMLConfigurationLoader configurationLoader) throws IOException {
        super("spider", configurationLoader);
    }

    @Override
    public void registerListeners() {
        Events.subscribe(PlayerMoveEvent.class)
                .filter(e -> DisguiseAbilities.spiders.contains(e.getPlayer()))
                .handler(e -> {
                        if (nearWeb(e.getTo().clone()) || nearWeb(e.getTo().clone().add(0,1,0))){
                            applyEffects(LocalPlayer.get(e.getPlayer()));
                    }
                });
    }
}
