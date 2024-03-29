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
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.io.IOException;

/**
 * @see me.aiglez.lonkskit.abilities.Ability
 * @author AigleZ
 * @date 11/10/2020
 */

public class HulkAbility extends FunctionalAbility {

    public HulkAbility(YAMLConfigurationLoader configurationLoader) throws IOException {
        super("hulk", configurationLoader);
    }

    @Override
    public void registerListeners() {
        Events.subscribe(PlayerInteractEntityEvent.class, EventPriority.HIGHEST)
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
                    }
                    e.setCancelled(true);
                });
        Events.subscribe(PlayerInteractEvent.class)
                .filter(AbilityPredicates.hasAbility(this))
                .filter(e -> e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK)
                .filter(e -> !e.getPlayer().getPassengers().isEmpty())
                .handler(e -> {
                    final Entity passenger = e.getPlayer().getPassengers().get(0);
                    LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());
                    if(passenger == null) {
                        localPlayer.msg("&cPassenger at index 0 was not found!");
                        return;
                    }

                    eject(e.getPlayer(), passenger);
                    passenger.sendMessage(ChatColor.translateAlternateColorCodes('&',"&b[DEBUG] &cYou have entered in combat with " + e.getPlayer().getDisplayName() + "."));
                    e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',"&b[DEBUG] &cYou have entered in combat with " + ((Player)passenger).getDisplayName() + "."));
                    boolean remove = Metadata.provideForEntity(passenger).remove(MetadataProvider.HULK_PICKED_UP);
                    localPlayer.msg("&cYou have ejected {0} (remove result: {1})", passenger.getName(), remove);
                });

        Events.subscribe(PlayerToggleSneakEvent.class)
                .filter(EventFilters.playerHasMetadata(MetadataProvider.HULK_PICKED_UP))
                .handler(e -> {
                    final LocalPlayer pickedUp = LocalPlayer.get(e.getPlayer());
                    if(pickedUp.toBukkit().isInsideVehicle()) {
                        pickedUp.toBukkit().getVehicle().eject();
                        pickedUp.getMetadata().remove(MetadataProvider.HULK_PICKED_UP);

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
