package me.aiglez.lonkskit.abilities.functional;

import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.FunctionalAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.MetadataProvider;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.config.ConfigurationNode;
import me.lucko.helper.event.filter.EventFilters;
import org.bukkit.entity.Entity;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.Vector;

/**
 * @author AigleZ
 * @date 11/10/2020
 */
public class HulkAbility extends FunctionalAbility {

    public HulkAbility( ConfigurationNode configuration) {
        super("hulk", configuration);
    }

    @Override
    public void registerListeners() {
        Events.subscribe(PlayerInteractEntityEvent.class)
                .filter(AbilityPredicates.hasAbility(this))
                .handler(e -> {
                    final LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());
                    final Entity rightClicked = e.getRightClicked();

                    if(!localPlayer.toBukkit().getPassengers().isEmpty()) {
                        localPlayer.msg("&a(Hulk - Debug) &cYou have already picked-up an entity!");
                        return;
                    }

                    localPlayer.toBukkit().addPassenger(rightClicked);
                    localPlayer.msg("&a(Hulk - Debug) &fYou have picked-up &a{0}", rightClicked.getName());
                });

        Events.subscribe(PlayerInteractEvent.class)
                .filter(AbilityPredicates.hasAbility(this))
                .filter(e -> e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR)
                .handler(e -> {
                    final LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());

                    if(localPlayer.toBukkit().getPassengers().isEmpty()) {
                        return;
                    }

                    final Entity pickedUp = localPlayer.toBukkit().getPassengers().get(0);
                    Schedulers.sync()
                            .runLater(() -> {
                                localPlayer.toBukkit().eject();

                                final Vector vector = localPlayer.getLocation().getDirection().multiply(1.5D);
                                pickedUp.setVelocity(vector);

                                localPlayer.msg("&a(Hulk - Debug) &fYou have &cejected &a{0}", pickedUp.getName());
                            }, 4L);
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
