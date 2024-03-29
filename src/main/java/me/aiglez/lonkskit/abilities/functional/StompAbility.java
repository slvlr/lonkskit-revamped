package me.aiglez.lonkskit.abilities.functional;

import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.FunctionalAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.lucko.helper.Events;
import me.lucko.helper.config.yaml.YAMLConfigurationLoader;
import me.lucko.helper.event.filter.EventFilters;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.io.IOException;


public class StompAbility extends FunctionalAbility {

    public StompAbility(YAMLConfigurationLoader configurationLoader) throws IOException {
        super("stomp", configurationLoader);
    }

    @Override
    public void registerListeners() {
        Events.subscribe(EntityDamageEvent.class)
                .filter(EventFilters.ignoreCancelled())
                .filter(e -> e.getCause() == EntityDamageEvent.DamageCause.FALL)
                .filter(AbilityPredicates.possiblyHasAbilities("stomp", "portastomp"))
                .handler(e -> {
                    final LocalPlayer localPlayer = LocalPlayer.get((Player) e.getEntity());

                    applyEffects(localPlayer);
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
                            under.msg(configuration.getNode("messages", "tip"));
                            localPlayer.msg(configuration.getNode("messages", "stomped"), under.getLastKnownName());
                            localPlayer.msg("&b[DEBUG] &cYou have entered in combat with " + under.getLastKnownName());
                            under.msg("&b[DEBUG] &cYou have entered in combat with " + localPlayer.getLastKnownName());
                        }
                    });
                    e.setDamage(Math.min(e.getDamage(), configuration.getNode("max-damage", "stomper").getDouble(4.5D)));
                });
    }
}
