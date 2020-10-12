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

import java.util.concurrent.TimeUnit;

/**
 * @author AigleZ
 * @date 05/10/2020
 */
public class JediAbility extends ItemStackAbility {

    private final ItemStack item;

    public JediAbility(ConfigurationNode configuration) {
        super("jedi", configuration);
        this.item = ItemStackBuilder.of(Material.SUNFLOWER)
                .name(configuration.getNode("item-name").getString("The Force"))
                .build();
    }

    @Override
    public ItemStack getItemStack() { return this.item; }

    @Override
    public boolean isItemStack(ItemStack item) { return this.item.isSimilar(item); }

    // --------------------------------------------------------------------------------------------
    @Override
    public void whenRightClicked(PlayerInteractEvent e) {
        e.setCancelled(true);
        final LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());

        if(!cooldown.test(localPlayer)){
            localPlayer.msg("&b[LonksKit] &cPlease wait, {0} second(s) left", cooldown.remainingTime(localPlayer, TimeUnit.SECONDS));
            return;
        }

        Location fromLocation = localPlayer.getLocation();
        WorldProvider.KP_WORLD.getNearbyPlayers(
                localPlayer.toBukkit().getLocation(),
                configuration.getNode("radius", "x-axis").getDouble(1D),
                configuration.getNode("radius", "y-axis").getDouble(1D),
                configuration.getNode("radius", "z-axis").getDouble(1D)
        ).forEach(player -> {
                    Location newLoc = player.getLocation().subtract(fromLocation.toVector());
                    Vector newVector = newLoc.toVector().normalize().multiply(
                            configuration.getNode("strength").getDouble(3)
                    );

                    player.setVelocity(Various.makeFinite(newVector));
                });
    }

    @Override
    public void whenLeftClicked(PlayerInteractEvent e) { e.setCancelled(true); }

    @Override
    public void handleListeners() { }
}
