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

public class FishermanAbility extends ItemStackAbility {

    private final ItemStack item;

    public FishermanAbility(ConfigurationNode configuration) {
        super("fisherman", configuration);
        this.item = ItemStackBuilder.of(Material.FISHING_ROD)
                .name("&eRod")
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

                    final Vector vector = playerLocation.subtract(caughtLocation).toVector().normalize().multiply(-2);

                    caught.setVelocity(vector);

                    localPlayer.msg("&3(Fisherman) Pushing {0} towards you", caught.getName());
                });
    }

}
