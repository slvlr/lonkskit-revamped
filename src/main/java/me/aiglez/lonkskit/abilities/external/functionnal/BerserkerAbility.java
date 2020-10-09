package me.aiglez.lonkskit.abilities.external.functionnal;

import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.FunctionalAbility;
import me.lucko.helper.Events;
import me.lucko.helper.config.ConfigurationNode;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

public class BerserkerAbility extends FunctionalAbility {

    public BerserkerAbility(ConfigurationNode configuration) {
        super("berserker", configuration);
    }

    @Override
    public void handleListeners() {
        Events.subscribe(PlayerDeathEvent.class)
                .filter(AbilityPredicates.isKillerhaveAbility(this))
                .handler(e -> {
                    int duration = getConfiguration().getNode("duration").getInt() * 20;
                    assert PotionType.STRENGTH.getEffectType() != null;
                    e.getEntity().getKiller().addPotionEffect(new PotionEffect(PotionType.STRENGTH.getEffectType(),duration,2));

                });
    }

}
