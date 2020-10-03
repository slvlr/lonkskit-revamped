package me.aiglez.lonkskit.listeners;

import me.aiglez.lonkskit.KitPlugin;
import me.aiglez.lonkskit.LonksKitProvider;
import me.aiglez.lonkskit.abilities.Ability;
import me.aiglez.lonkskit.abilities.functionnal.StompAbility;
import me.aiglez.lonkskit.abilities.itembased.PortastompAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.Logger;
import me.aiglez.lonkskit.utils.Metadatas;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerListeners implements Listener {

    public PlayerListeners(KitPlugin plugin) {
        plugin.registerListener(this);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        final Entity entity = e.getEntity();
        if(entity instanceof Player) {
            final LocalPlayer localPlayer = LonksKitProvider.getPlayerFactory().getLocalPlayer((Player) entity);
            if(!localPlayer.hasSelectedKit()) return;


            // no fall damage
            if(e.getCause() == EntityDamageEvent.DamageCause.FALL) {
                if(localPlayer.metadata().has(Metadatas.PLAYER_NO_FALL_DAMAGE)) {
                    localPlayer.metadata().remove(Metadatas.PLAYER_NO_FALL_DAMAGE);
                    e.setCancelled(true);
                    return;
                }

                Logger.debug("Handling fall damage of the player " + localPlayer.getLastKnownName());
                final StompAbility stompAbility = (StompAbility) Ability.get("stomp");
                final PortastompAbility portastompAbility = (PortastompAbility) Ability.get("portastomp");

                if(localPlayer.getNullableSelectedKit().hasAbility(stompAbility) || localPlayer.getNullableSelectedKit().hasAbility(portastompAbility)) {
                    stompAbility.handleLanding(localPlayer, e);
                    return;
                }

            } else if(e.getCause() == EntityDamageEvent.DamageCause.LIGHTNING) {
                if(localPlayer.metadata().has(Metadatas.PLAYER_NO_LIGHTING_DAMAGE)) {
                    e.setDamage(0);
                    e.setCancelled(true);
                }
            }
        }
    }
}
