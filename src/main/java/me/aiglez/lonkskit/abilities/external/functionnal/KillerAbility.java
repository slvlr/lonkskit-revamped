package me.aiglez.lonkskit.abilities.external.functionnal;

import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.FunctionalAbility;
import me.lucko.helper.Events;
import me.lucko.helper.config.ConfigurationNode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class KillerAbility extends FunctionalAbility {

    public KillerAbility(ConfigurationNode configuration) {
        super("killer", configuration);
    }

    @Override
    public void handleListeners() {
        Events.subscribe(PlayerDeathEvent.class)
                .filter(AbilityPredicates.humanHasAbility(this))
                .handler(e->{
                    Player player = (Player) e.getEntity();
                    TNTPrimed tnt = (TNTPrimed) e.getEntity().getLocation().getWorld()
                            .spawnEntity(e.getEntity().getLocation(), EntityType.PRIMED_TNT);
                    tnt.setFuseTicks(5);

                });
    }
}
