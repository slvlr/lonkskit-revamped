package me.aiglez.lonkskit.abilities.external.functionnal;

import me.aiglez.lonkskit.KitPlugin;
import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.FunctionalAbility;
import me.aiglez.lonkskit.abilities.external.itembased.SonicAbility;
import me.lucko.helper.Events;
import me.lucko.helper.config.ConfigurationNode;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

import java.io.IOException;

public class BerserkerAbility extends FunctionalAbility {

    public BerserkerAbility(ConfigurationNode configuration) {
        super("berserker", configuration);
    }

    @Override
    public void handleListeners() {
        Events.subscribe(PlayerDeathEvent.class)
                .filter(AbilityPredicates.isKillerhaveAbility(this))
                .handler(e -> {
                    FileConfiguration config = null;
                    try {
                        config = SonicAbility.getConfig("berserker");
                    } catch (IOException | InvalidConfigurationException ioException) {
                        System.out.println("BERSERKER ERROR");
                    }
                    e.getEntity().getKiller().addPotionEffect(new PotionEffect(PotionType.STRENGTH.getEffectType(),400,2));


                });
    }

}
