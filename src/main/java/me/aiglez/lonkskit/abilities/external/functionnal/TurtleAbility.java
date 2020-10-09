package me.aiglez.lonkskit.abilities.external.functionnal;

import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.FunctionalAbility;
import me.lucko.helper.Events;
import me.lucko.helper.config.ConfigurationNode;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class TurtleAbility extends FunctionalAbility {

    public TurtleAbility(ConfigurationNode configuration) {
        super("turtle", configuration);
    }

    @Override
    public void handleListeners() {
        Events.subscribe(EntityDamageByEntityEvent.class)
                .filter(AbilityPredicates.humanHasAbility(this))
                .filter(e -> e.getDamager() instanceof Player && e.getEntity() instanceof Player)
                .handler(e -> {
                    Player player = (Player) e.getEntity();
                    e.setDamage(0);
                    player.damage(1);
                });

    }
}

