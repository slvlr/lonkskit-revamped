package me.aiglez.lonkskit.abilities.functional.johan.disguises;

import me.aiglez.lonkskit.KitPlugin;
import me.aiglez.lonkskit.abilities.FunctionalAbility;
import me.aiglez.lonkskit.abilities.functional.johan.DisguiseAbilities;
import me.lucko.helper.Events;
import me.lucko.helper.config.yaml.YAMLConfigurationLoader;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.io.IOException;

public class CreeperAbility extends FunctionalAbility {
    private int times2 = 5;
    public CreeperAbility(YAMLConfigurationLoader configurationLoader) throws IOException {
        super("creeper", configurationLoader);
    }

    @Override
    public void registerListeners() {
        Events.subscribe(EntityDamageByEntityEvent.class)
                .filter(e -> e.getEntity() instanceof Player && e.getDamager() instanceof Player)
                .filter(e -> DisguiseAbilities.creepers.contains(e.getDamager()))
                .handler(e ->{
                    if (times2 == 0) {
                        e.setCancelled(true);
                        Bukkit.getScheduler().runTaskLater(KitPlugin.getSingleton(), () -> {
                            e.getEntity().setVelocity(e.getEntity().getLocation().getDirection().multiply(getConfiguration().getNode("creeper-launch").getDouble(1.5D)).setY(3D));
                        },1L);
                    }
                    if (times2 >= 10) {
                        times2 = 0;
                    }else
                        times2++;
                });
    }
}
