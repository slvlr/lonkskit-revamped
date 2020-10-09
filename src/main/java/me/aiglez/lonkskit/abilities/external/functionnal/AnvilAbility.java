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
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
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
                .handler(a -> {
                    Player victim = (Player) a.getEntity();
                    if (a.getDamager() instanceof Player) {
                        a.setCancelled(true);
                        victim.damage(a.getDamage());
                    }else if (a.getDamager() instanceof Projectile){
                        if (a.getDamager() instanceof AbstractArrow){
                            Arrow arrow = (Arrow) a.getDamager();
                            arrow.setKnockbackStrength(-10);
                        }else{
                            Arrow arrow = (Arrow) a.getDamager();
                            arrow.setKnockbackStrength(-10);
                            System.out.println("HEY RANGEWONK PLEASE TELL ME THIS WORD IN THE CHAT NIHAA");
                        }
                    }


                });


    }

}
