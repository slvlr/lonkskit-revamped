package me.aiglez.lonkskit.abilities.external.functionnal;

import me.aiglez.lonkskit.KitPlugin;
import me.aiglez.lonkskit.abilities.Ability;
import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.FunctionalAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.config.ConfigurationNode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

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
                    final Vector vec = new Vector();
                    Bukkit.getScheduler().scheduleSyncDelayedTask(KitPlugin.getSingleton(), new Runnable() {
                        @Override
                        public void run() {
                            a.getEntity().setVelocity(vec);
                        }
                    },1L);
                    a.getEntity().setVelocity(vec);


                });

    }
}
