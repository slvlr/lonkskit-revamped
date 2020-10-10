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
import org.bukkit.Sound;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class AnvilAbility extends FunctionalAbility {

    public AnvilAbility(ConfigurationNode configuration) {
        super("anvil", configuration);
    }
    private int i;

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
                            a.setCancelled(true);
                            i++;
                            victim.setHealth(victim.getHealth() - a.getDamage());
                            a.getDamager().remove();
                            victim.setArrowsInBody(i);
                            victim.playSound(victim.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER,12F,1F);
                        }else{
                        System.out.println("HEY RANGEWONK PLEASE TELL ME THIS WORD IN THE CHAT NIHAA");
                        }
                    }


                });
        Events.subscribe(PlayerDeathEvent.class)
                .filter(AbilityPredicates.humanHasAbility(this))
                .handler(event -> i = 0);
        
        
        



    }

}
