package me.aiglez.lonkskit.abilities.functional;

import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.FunctionalAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.Logger;
import me.aiglez.lonkskit.utils.MetadataProvider;
import me.lucko.helper.Events;
import me.lucko.helper.config.yaml.YAMLConfigurationLoader;
import me.lucko.helper.event.filter.EventFilters;
import me.lucko.helper.metadata.Metadata;
import me.lucko.helper.metadata.SoftValue;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

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

                    final Player bukkit = localPlayer.toBukkit();
                    Logger.debug("Fired");
                    if(bukkit.getPassengers().isEmpty()) {
                        // no passengers pick up the entity
                        bukkit.addPassenger(rightClicked);

                        Metadata.provideForEntity(rightClicked).put(MetadataProvider.HULK_PICKED_UP, SoftValue.of(true));
                        localPlayer.msg("&eYou have picked up {0}", rightClicked.getName());
                    } else {
                        final Entity passenger = bukkit.getPassengers().get(0);
                        if(passenger == null) {
                            localPlayer.msg("&cPassenger at index 0 was not found!");
                            return;
                        }

                        eject(bukkit, passenger);

                        boolean remove = Metadata.provideForEntity(passenger).remove(MetadataProvider.HULK_PICKED_UP);
                        localPlayer.msg("&cYou have ejected {0} (remove result: {1})", passenger.getName(), remove);
                    }
                    e.setCancelled(true);
                });

        Events.subscribe(PlayerToggleSneakEvent.class)
                .filter(EventFilters.playerHasMetadata(MetadataProvider.HULK_PICKED_UP))
                .handler(e -> {
                    final LocalPlayer pickedUp = LocalPlayer.get(e.getPlayer());
                    if(pickedUp.toBukkit().isInsideVehicle()) {
                        pickedUp.toBukkit().getVehicle().eject();
                        pickedUp.metadata().remove(MetadataProvider.HULK_PICKED_UP);

                        pickedUp.msg("&a(Hulk - Debug) &fYou have &cescaped &f!");
                    }
                });

        
    }
    
    private void eject(Player vehicle, Entity entity) {
        vehicle.eject();
        entity.setVelocity(vehicle.getLocation().getDirection().multiply(1.5D).setY(
                vehicle.getLocation().getDirection().getY() * 0.75
        ));
    }
}
