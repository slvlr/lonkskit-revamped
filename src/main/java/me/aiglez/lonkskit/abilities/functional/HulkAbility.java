package me.aiglez.lonkskit.abilities.functional;

import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.FunctionalAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.MetadataProvider;
import me.lucko.helper.Events;
import me.lucko.helper.config.yaml.YAMLConfigurationLoader;
import me.lucko.helper.event.filter.EventFilters;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.Vector;

import java.io.IOException;

/**
 * @author AigleZ
 * @date 11/10/2020
 */

public class HulkAbility extends FunctionalAbility {

    public HulkAbility(YAMLConfigurationLoader configurationLoader) throws IOException {
        super("hulk", configurationLoader);
    }

    @Override
    public void registerListeners() {
        Events.subscribe(PlayerInteractEntityEvent.class)
                .filter(AbilityPredicates.hasAbility(this))
                .handler(e -> {
                    final LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());
                    final Entity rightClicked = e.getRightClicked();

                    if(!localPlayer.toBukkit().getPassengers().isEmpty()) {
                        final Entity passenger = localPlayer.toBukkit().getPassengers().get(0);
                        if(passenger == null) return;

                        applyEffects(localPlayer);

                        if(passenger.getUniqueId().equals(rightClicked.getUniqueId())) {
                            localPlayer.toBukkit().eject();

                            final Vector vector = localPlayer.getLocation().getDirection().multiply(1.5D);
                            passenger.setVelocity(vector);

                            localPlayer.msg("&a(Hulk - Debug) &fYou have &cejected &a{0}.", passenger.getName());
                            return;
                        }

                        localPlayer.msg("&a(Hulk - Debug) &cYou have already picked-up an entity!");
                        return;
                    }

                    localPlayer.toBukkit().addPassenger(rightClicked);
                    localPlayer.msg("&a(Hulk - Debug) &fYou have picked-up &a{0}", rightClicked.getName());
                });

        Events.subscribe(PlayerToggleSneakEvent.class)
                .filter(EventFilters.playerHasMetadata(MetadataProvider.HULK_PICKED_UP))
                .handler(e -> {
                    final LocalPlayer pickedUp = LocalPlayer.get(e.getPlayer());
                    if(pickedUp.toBukkit().isInsideVehicle()) {
                        pickedUp.toBukkit().getVehicle().eject();

                        pickedUp.msg("&a(Hulk - Debug) &fYou have &cescaped &f!");
                    }
                });

    }
}
