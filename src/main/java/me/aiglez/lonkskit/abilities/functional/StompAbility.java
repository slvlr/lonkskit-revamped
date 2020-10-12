package me.aiglez.lonkskit.abilities.functional;

import me.aiglez.lonkskit.abilities.Ability;
import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.FunctionalAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
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

                    localPlayer.toBukkit().getWorld().getNearbyPlayers(
                            localPlayer.toBukkit().getLocation(),
                            configuration.getNode("radius", "x-axis").getDouble(1D),
                            configuration.getNode("radius", "y-axis").getDouble(1D),
                            configuration.getNode("radius", "z-axis").getDouble(1D)
                    ).forEach(player -> {

                        final LocalPlayer under = LocalPlayer.get(player);
                        if(!localPlayer.toBukkit().hasLineOfSight(under.toBukkit())) return;
                        if(!(under.getUniqueId().equals(localPlayer.getUniqueId()))) {
                            double damage;
                            if(under.toBukkit().isSneaking()) { // is player sneaking
                                damage = Math.min(e.getFinalDamage(), configuration.getNode("max-damage", "target-sneaking").getDouble(4.5D));
                            } else {
                                damage = e.getFinalDamage();
                            }
                            under.toBukkit().damage(damage);
                            under.msg(configuration.getNode("messages", "tip").getString("Message tip Null"));
                        }
                    });
                    e.setDamage(Math.min(e.getDamage(), configuration.getNode("max-damage", "stomper").getDouble(4.5D)));
                });
    }
}
