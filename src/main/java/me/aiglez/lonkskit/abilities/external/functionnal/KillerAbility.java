package me.aiglez.lonkskit.abilities.external.functionnal;

import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.FunctionalAbility;
import me.lucko.helper.Events;
import me.lucko.helper.config.ConfigurationNode;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TNTPrimed;
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
                    Location loc = e.getEntity().getLocation();    //GET LOCATION WHEN THE PLAYER DIES :)
                    TNTPrimed tnt = (TNTPrimed) loc.getWorld().spawnEntity(loc,EntityType.PRIMED_TNT); //BACK IN BLACK <3
                    tnt.setFuseTicks(70);
                });
    }
}
