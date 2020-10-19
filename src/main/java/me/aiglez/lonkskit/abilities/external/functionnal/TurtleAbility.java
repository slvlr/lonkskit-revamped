package me.aiglez.lonkskit.abilities.external.functionnal;

import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.FunctionalAbility;
import me.lucko.helper.Events;
import me.lucko.helper.config.ConfigurationNode;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class TurtleAbility extends FunctionalAbility {

    public TurtleAbility(ConfigurationNode configuration) {
        super("turtle", configuration);
    }

    @Override
    public void handleListeners() {
        Events.subscribe(EntityDamageByEntityEvent.class)
                .filter(AbilityPredicates.humanHasAbility(this))
                .handler(e -> {
                    Player player = (Player) e.getEntity();
                    if (player.isSneaking()){
                        e.setDamage(0); //7it setDamage katzid tn9s b armor so ila atdir 1 rah maghaytn9s walo (aji l3ndi ndir lk sway3 XD )
                        player.setHealth(player.getHealth() - 1);
                    }
                });

    }
}

