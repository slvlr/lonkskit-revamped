package me.aiglez.lonkskit.abilities.functional.johan;

import com.google.common.base.Preconditions;
import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.FunctionalAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.lucko.helper.Events;
import me.lucko.helper.config.yaml.YAMLConfigurationLoader;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.io.IOException;

public class KillerAbility extends FunctionalAbility {

    public KillerAbility(YAMLConfigurationLoader configurationLoader) throws IOException {
        super("killer", configurationLoader);
    }

    @Override
    public void registerListeners() {
        Events.subscribe(PlayerDeathEvent.class)
                .filter(AbilityPredicates.possiblyHasAbility(this))
                .handler(e -> {
                    Preconditions.checkNotNull(e.getEntity().getLocation().getWorld());
                    TNTPrimed tnt = (TNTPrimed) e.getEntity().getLocation().getWorld()
                            .spawnEntity(e.getEntity().getLocation(), EntityType.PRIMED_TNT);

                    tnt.setYield(configuration.getNode("yield").getFloat(4.0F));
                    tnt.setFuseTicks(5);

                    applyEffects(LocalPlayer.get(e.getEntity()));
                });
    }
}
