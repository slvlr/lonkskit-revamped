package me.aiglez.lonkskit.abilities.functional;

import me.aiglez.lonkskit.abilities.Ability;
import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.FunctionalAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.Logger;
import me.lucko.helper.Events;
import me.lucko.helper.config.ConfigurationNode;
import me.lucko.helper.event.filter.EventFilters;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

public class StompAbility extends FunctionalAbility {

    public StompAbility(ConfigurationNode configuration) {
        super("stomp", configuration);
    }

    @Override
    public void handleListeners() {
        Events.subscribe(EntityDamageEvent.class)
                .filter(EventFilters.ignoreCancelled())
                .filter(e -> e.getCause() == EntityDamageEvent.DamageCause.FALL)
                .filter(e -> {
                    if(AbilityPredicates.humanHasAbility(this).test(e)) return true;
                    if(Ability.get("portastomp") != null){
                        return AbilityPredicates.humanHasAbility(Ability.get("portastomp")).test(e);
                    }
                    return false;
                })
                .handler(e -> {
                    final LocalPlayer localPlayer = LocalPlayer.get((Player) e.getEntity());

                    localPlayer.toBukkit().getWorld().getNearbyEntities(
                            localPlayer.toBukkit().getLocation(),
                            5, 5, 5,
                            entity -> entity instanceof Player
                    ).forEach(entity -> {
                        final LocalPlayer under = LocalPlayer.get(((Player) entity));
                        if(!localPlayer.toBukkit().hasLineOfSight(under.toBukkit())) {
                            localPlayer.msg("&6(Stomp) &fNear: " + under.getLastKnownName() + " &cnot in line of sight");
                            return;
                        }
                        if(!(under.getUniqueId().equals(localPlayer.getUniqueId()))) {
                            double damage;
                            if(under.toBukkit().isSneaking()) { // is player sneaking
                                damage = Math.min(e.getFinalDamage(), 4.5);
                                Logger.debug("[Stomp/Portastomp] Damaging a player with " + damage + " damage (SNEAKING)");
                            } else {
                                damage = e.getFinalDamage();
                                Logger.debug("[Stomp/Portastomp] Damaging a player with " + damage + " damage");
                            }

                            under.toBukkit().damage(damage);
                            localPlayer.msg("&6(Stomp) &eDamaging the player {0} with {1} damage", under.getLastKnownName(), damage);
                        }
                    });
                    Logger.debug("[Stomp/Portastomp] Setting damage of player who use the ability to " + Math.min(e.getDamage(), 2.5D) );
                    e.setDamage(Math.min(e.getDamage(), 2.5D));
                });
    }
}