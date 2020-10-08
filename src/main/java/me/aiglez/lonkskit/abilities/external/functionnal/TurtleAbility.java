package me.aiglez.lonkskit.abilities.external.functionnal;

import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.FunctionalAbility;
import me.lucko.helper.Events;
import me.lucko.helper.config.ConfigurationNode;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

public class TurtleAbility extends FunctionalAbility {

    public TurtleAbility(ConfigurationNode configuration) {
        super("turtle", configuration);
    }

    @Override
    public void handleListeners() {
        Events.subscribe(EntityDamageEvent.class)
                .filter(e -> e.getEntity() instanceof Player)
                .filter(AbilityPredicates.humanHasAbility(this))
                .handler(e -> {
                    Player player = (Player) e.getEntity();
                    if (e.getCause() != EntityDamageEvent.DamageCause.FALL) {
                        if (player.isSneaking()) {
                            e.setCancelled(true);
                            ((Player) e.getEntity()).damage(1);
                        }
                    }
                });
    }
}

