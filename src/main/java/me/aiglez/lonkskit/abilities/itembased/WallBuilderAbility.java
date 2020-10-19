package me.aiglez.lonkskit.abilities.itembased;

import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.abilities.helpers.ConstructHelper;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.lucko.helper.config.ConfigurationNode;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEvent;

public class WallBuilderAbility extends ItemStackAbility {

    public WallBuilderAbility(ConfigurationNode configuration) {
        super("wallbuilder", configuration);
    }

    @Override
    public void whenRightClicked(PlayerInteractEvent e) {
        e.setCancelled(true);
        final LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());
        final Block clickedBlock = e.getClickedBlock();

        if(clickedBlock == null) {
            localPlayer.msg("&a(WallBuilder - Debug) &cYou must click on a block");
            return;
        }

        final boolean result = ConstructHelper.buildWallAt(localPlayer.getLocation().getYaw(), clickedBlock.getLocation());
        if(result == false) {
            localPlayer.msg("&a(WallBuilder - Debug) &cYou can't build there!");
            return;
        }

        localPlayer.msg("&a(WallBuilder - Debug) &aSuccessfully placed a wall");
    }

    @Override
    public void whenLeftClicked(PlayerInteractEvent e) {
        e.setCancelled(true);
    }

    @Override
    public void registerListeners() { }
}
