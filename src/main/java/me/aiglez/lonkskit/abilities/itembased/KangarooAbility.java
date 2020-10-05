package me.aiglez.lonkskit.abilities.itembased;

import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.items.ItemStackBuilder;
import me.lucko.helper.config.ConfigurationNode;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author AigleZ
 * @date 04/10/2020
 */
public class KangarooAbility extends ItemStackAbility {

    private final ItemStack item;

    public KangarooAbility(ConfigurationNode configuration) {
        super("kangaroo", configuration);
        this.item = ItemStackBuilder.of(Material.EMERALD)
                .name("&6Kangaroo")
                .build();
    }

    @Override
    public ItemStack getItemStack() { return this.item; }

    @Override
    public boolean isItemStack(ItemStack item) { return this.item.isSimilar(item); }

    @Override
    public void whenRightClicked(PlayerInteractEvent e) {
        e.setCancelled(true);
        final LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());

        if(localPlayer.toBukkit().isSneaking()) {
            localPlayer.toBukkit().setVelocity(localPlayer.getLocation().getDirection().multiply(1.2d).setY(0.3d));
        } else {
            localPlayer.toBukkit().setVelocity(localPlayer.getLocation().getDirection().multiply(1.1d).setY(0.6d));
        }
    }

    @Override
    public void whenLeftClicked(PlayerInteractEvent e) { }

    @Override
    public void handleListeners() { }
}
