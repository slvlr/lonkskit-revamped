package me.aiglez.lonkskit.abilities.functional.disguises;

import me.aiglez.lonkskit.abilities.FunctionalAbility;
import me.aiglez.lonkskit.abilities.functional.DisguiseAbilities;
import me.lucko.helper.Events;
import me.lucko.helper.config.yaml.YAMLConfigurationLoader;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerMoveEvent;

import java.io.IOException;

public class MooshroomAbility extends FunctionalAbility {
    private static int times = 0;

    public MooshroomAbility(YAMLConfigurationLoader configurationLoader) throws IOException {
        super("mooshroom", configurationLoader);
    }

    @Override
    public void registerListeners() {
        Events.subscribe(PlayerMoveEvent.class)
                .filter(e -> DisguiseAbilities.mooshrooms.contains(e.getPlayer()))
                .filter(e -> e.getTo().getBlock().getType() == Material.MYCELIUM || e.getFrom().getBlock().getType() == Material.MYCELIUM
                        || e.getTo().getBlock().getRelative(0,-1,0).getType() == Material.MYCELIUM ||  e.getFrom().getBlock().getRelative(0,-1,0).getType() == Material.MYCELIUM)
                .handler(e -> {
                    if (times == 0){
                        if (e.getPlayer().getHealth() != 20){
                            e.getPlayer().setHealth(Math.min(20.00D,e.getPlayer().getHealth() + 1));
                        }
                    }
                    if (times >= 20){ times = 0;}
                    times++;
                });
    }
}
