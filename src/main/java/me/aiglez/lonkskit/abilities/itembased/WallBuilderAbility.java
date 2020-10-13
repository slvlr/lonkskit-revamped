package me.aiglez.lonkskit.abilities.itembased;

import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.abilities.helpers.ConstructHelper;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.items.ItemStackBuilder;
import me.lucko.helper.Schedulers;
import me.lucko.helper.config.ConfigurationNode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class WallBuilderAbility extends ItemStackAbility {

    private final ItemStack item;

    public WallBuilderAbility(ConfigurationNode configuration) {
        super("wallbuilder", configuration);
        this.item = ItemStackBuilder.of(Material.BRICK)
                .name("&bWall")
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
        final Block clickedBlock = e.getClickedBlock();

        if(clickedBlock == null) {
            localPlayer.msg("&6(WallBuilder - Debug) &cYou must click on a block");
            return;
        }

        Schedulers.async().run(() -> {
            localPlayer.msg("&6(WallBuilder - Debug) &aBuild result: " + ConstructHelper.buildWallAt(clickedBlock.getLocation()));
        });
    }

    @Override
    public void whenLeftClicked(PlayerInteractEvent e) {
        e.setCancelled(true);
    }

    @Override
    public void handleListeners() {

    }
}
