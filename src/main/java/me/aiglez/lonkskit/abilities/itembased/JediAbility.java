package me.aiglez.lonkskit.abilities.itembased;

import me.aiglez.lonkskit.WorldProvider;
import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.Various;
import me.aiglez.lonkskit.utils.items.ItemStackBuilder;
import me.lucko.helper.config.ConfigurationNode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author AigleZ
 * @date 05/10/2020
 */
public class JediAbility extends ItemStackAbility {

    private final ItemStack item;

    public JediAbility(ConfigurationNode configuration) {
        super("jedi", configuration);
        this.item = ItemStackBuilder.of(Material.SUNFLOWER)
                .name("&cThe force")
                .build();
    }

    @Override
    public ItemStack getItemStack() { return this.item; }

    @Override
    public boolean isItemStack(ItemStack item) { return this.item.isSimilar(item); }

    // --------------------------------------------------------------------------------------------
    @Override
    public void whenRightClicked(PlayerInteractEvent e) {
        final LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());

        Location fromLocation = localPlayer.getLocation();

        AtomicInteger pushed = new AtomicInteger();
        WorldProvider.KP_WORLD.getNearbyEntities(localPlayer.getLocation(), 6, 6, 6).stream()
                .forEach(entity -> {
                    Location newLoc = entity.getLocation().subtract(fromLocation.toVector());
                    Vector newV = newLoc.toVector().normalize().multiply(2);

                    entity.setVelocity(Various.makeFinite(newV));
                    pushed.getAndIncrement();
                });

        localPlayer.msg("&0(Jedi) &8Pushed a way entities [number: {0}]" + pushed.get());
    }

    @Override
    public void whenLeftClicked(PlayerInteractEvent e) { }

    @Override
    public void handleListeners() { }
}
