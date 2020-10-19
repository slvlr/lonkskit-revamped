package me.aiglez.lonkskit.abilities.functional.johan;

import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.FunctionalAbility;
import me.lucko.helper.Events;
import me.lucko.helper.config.yaml.YAMLConfigurationLoader;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.io.IOException;

public class TurtleAbility extends FunctionalAbility {

    public TurtleAbility(YAMLConfigurationLoader configurationLoader) throws IOException {
        super("turtle", configurationLoader);
    }

    @Override
    public void registerListeners() {
        Events.subscribe(EntityDamageByEntityEvent.class)
                .filter(AbilityPredicates.possiblyHasAbility(this))
                .handler(e -> {
                    Player player = (Player) e.getEntity();
                    if (player.isSneaking()){
                        e.setDamage(0); //7it setDamage katzid tn9s b armor so ila atdir 1 rah maghaytn9s walo (aji l3ndi ndir lk sway3 XD )
                        player.setHealth(player.getHealth() - 1);
                    }
                });
    }
}
