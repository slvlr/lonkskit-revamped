package me.aiglez.lonkskit.abilities.external.functionnal;

import com.google.common.base.Preconditions;
import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.FunctionalAbility;
import me.lucko.helper.Events;
import me.lucko.helper.config.ConfigurationNode;
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
                    Preconditions.checkNotNull(e.getEntity().getLocation().getWorld());
                    TNTPrimed tnt = (TNTPrimed) e.getEntity().getLocation().getWorld()
                            .spawnEntity(e.getEntity().getLocation(), EntityType.PRIMED_TNT);
                    tnt.setFuseTicks(5);

                });
    }
}
