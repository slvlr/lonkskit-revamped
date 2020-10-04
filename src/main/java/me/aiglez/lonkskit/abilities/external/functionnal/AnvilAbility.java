package me.aiglez.lonkskit.abilities.external.functionnal;

import me.aiglez.lonkskit.abilities.Ability;
import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.FunctionalAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.lucko.helper.Events;
import me.lucko.helper.config.ConfigurationNode;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class AnvilAbility extends FunctionalAbility {

    public AnvilAbility(ConfigurationNode configuration) {
        super("anvil", configuration);
    }
        

    @Override
    public void handleListeners() {
        Events.subscribe(EntityDamageByEntityEvent.class)
                .filter(AbilityPredicates.humanHasAbility(this))
                .filter(e -> e.getEntity() instanceof Player && e.getDamager() instanceof Player )
                .handler(a -> {
                    a.setCancelled(true);
                    double damage = a.getDamage();
                    Player damager = (Player) a.getDamager();
                    Player victim = (Player) a.getEntity();
                    victim.damage(damage,damager);
                });

    }
}
