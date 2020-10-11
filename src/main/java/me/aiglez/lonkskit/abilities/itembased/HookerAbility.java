package me.aiglez.lonkskit.abilities.itembased;

import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.items.ItemStackBuilder;
import me.lucko.helper.Events;
import me.lucko.helper.config.ConfigurationNode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class HookerAbility extends ItemStackAbility {

    private final ItemStack item;

    public HookerAbility(ConfigurationNode configuration) {
        super("hooker", configuration);
        this.item = ItemStackBuilder.of(Material.FISHING_ROD)
                .name(configuration.getNode("item-name").getString("Rod"))
                .build();
    }

    @Override
    public ItemStack getItemStack() { return this.item; }

    @Override
    public boolean isItemStack(ItemStack item) { return this.item.isSimilar(item); }

    @Override
    public void whenRightClicked(PlayerInteractEvent e) { }

    @Override
    public void whenLeftClicked(PlayerInteractEvent e) { }

    @Override
    public void handleListeners() {
        Events.subscribe(PlayerFishEvent.class)
                .filter(AbilityPredicates.playerHasAbility(this))
                .filter(e -> e.getCaught() != null)
                .handler(e -> {
                    final LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());
                    final Entity caught = e.getCaught();

                    final Location caughtLocation = caught.getLocation();
                    final Location playerLocation = localPlayer.getLocation();

                    final Vector vector = caughtLocation.subtract(playerLocation).toVector().normalize().multiply(
                            configuration.getNode("speed").getDouble(2D)
                    );

                    localPlayer.toBukkit().setVelocity(vector);
                });
    }

}
