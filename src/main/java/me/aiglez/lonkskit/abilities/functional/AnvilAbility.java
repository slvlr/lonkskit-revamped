package me.aiglez.lonkskit.abilities.functional;

import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.FunctionalAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.lucko.helper.Events;
import me.lucko.helper.config.yaml.YAMLConfigurationLoader;
import org.bukkit.Sound;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.projectiles.ProjectileSource;

import java.io.IOException;

public class AnvilAbility extends FunctionalAbility {

    private int i;

    public AnvilAbility(YAMLConfigurationLoader configurationLoader) throws IOException {
        super("anvil", configurationLoader);
    }

    @Override
    public void registerListeners() {
        Events.subscribe(EntityDamageByEntityEvent.class)
                .filter(AbilityPredicates.possiblyHasAbility(this))
                .handler(a -> {
                    Player victim = (Player) a.getEntity();
                    if (a.getDamager() instanceof Player) {
                        a.setCancelled(true);
                        victim.damage(a.getDamage());

                        applyEffects(LocalPlayer.get((Player) a.getDamager()));
                    }else if (a.getDamager() instanceof Projectile){
                        if (a.getDamager() instanceof AbstractArrow){
                            a.setCancelled(true);
                            i++;
                            victim.setHealth(victim.getHealth() - a.getDamage());
                            a.getDamager().remove();
                            victim.setArrowsInBody(i);
                            victim.playSound(victim.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER,12F,1F);
                            ProjectileSource source = ((AbstractArrow) a.getDamager()).getShooter();
                            if (source instanceof Player){
                                ((Player) source).playSound(((Player) source).getLocation(),Sound.ENTITY_ARROW_HIT_PLAYER,12F,1F);
                                applyEffects(LocalPlayer.get((Player) source));
                            }

                        }else{
                            System.out.println("HEY RANGEWONK PLEASE TELL ME THIS WORD IN THE CHAT NIHAA");
                        }
                    }


                });
        Events.subscribe(PlayerDeathEvent.class)
                .filter(AbilityPredicates.possiblyHasAbility(this))
                .handler(event -> i = 0);
    }
}
