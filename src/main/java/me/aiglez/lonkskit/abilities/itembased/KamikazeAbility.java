package me.aiglez.lonkskit.abilities.itembased;

import me.aiglez.lonkskit.WorldProvider;
import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.items.ItemStackBuilder;
import me.lucko.helper.config.ConfigurationNode;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class KamikazeAbility extends ItemStackAbility {

    private final ItemStack item;
    private final static float TNT_POWER = 4F;

    public KamikazeAbility(ConfigurationNode configuration) {
        super("kamikaze", configuration);
        this.item = ItemStackBuilder.of(Material.GUNPOWDER)
                .name(configuration.getNode("item-name").getString("Kamikaze"))
                .build();
    }

    @Override
    public ItemStack getItemStack() {
        return this.item;
    }

    @Override
    public boolean isItemStack(ItemStack item) {
        return this.item.isSimilar(item);
    }

    @Override
    public void whenRightClicked(PlayerInteractEvent e) {
        e.setCancelled(true);
        final LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());

        localPlayer.toBukkit().damage(999); // we kill the player :(  (#suicide)
        localPlayer.msg("&4(Kamikaze - Debug) &cYou have exploded!");
        WorldProvider.KP_WORLD.createExplosion(
                localPlayer.getLocation(),
                TNT_POWER,
                false,
                false,
                localPlayer.toBukkit()
        );
    }

    @Override
    public void whenLeftClicked(PlayerInteractEvent e) {
        e.setCancelled(true);
    }

    @Override
    public void handleListeners() {

    }
}
