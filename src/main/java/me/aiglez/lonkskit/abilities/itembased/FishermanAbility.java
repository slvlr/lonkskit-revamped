package me.aiglez.lonkskit.abilities.itembased;

import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.lucko.helper.Events;
import me.lucko.helper.config.ConfigurationNode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class FishermanAbility extends ItemStackAbility {

    public FishermanAbility(ConfigurationNode configuration) {
        super("fisherman", configuration);
    }

    @Override
    public void whenRightClicked(PlayerInteractEvent e) { }

    @Override
    public void whenLeftClicked(PlayerInteractEvent e) { }

    @Override
    public void registerListeners() {
        Events.subscribe(PlayerFishEvent.class)
                .filter(AbilityPredicates.hasAbility(this))
                .filter(e -> e.getCaught() != null)
                .handler(e -> {
                    final LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());
                    final Entity caught = e.getCaught();

                    applyEffects(localPlayer);

                    final Location caughtLocation = caught.getLocation();
                    final Location playerLocation = localPlayer.getLocation();

                    final Vector vector = playerLocation.subtract(caughtLocation).toVector().normalize().multiply(
                            configuration.getNode("speed").getDouble(2D)
                    );

                    caught.setVelocity(vector);
                });
    }

}
