package me.aiglez.lonkskit.abilities.itembased;

import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.abilities.helpers.ConstructHelper;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.lucko.helper.config.yaml.YAMLConfigurationLoader;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.IOException;

public class WallBuilderAbility extends ItemStackAbility {

    public WallBuilderAbility(YAMLConfigurationLoader configurationLoader) throws IOException {
        super("wallbuilder", configurationLoader);
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

        final boolean result = ConstructHelper.buildWallAt(localPlayer.getLocation().getYaw(), clickedBlock);
        if(result) {
            localPlayer.msg("&a(WallBuilder - Debug) &aSuccessfully placed a wall");
        } else {
            localPlayer.msg("&a(WallBuilder - Debug) &cYou can't build there!");
        }
    }

    @Override
    public void whenLeftClicked(PlayerInteractEvent e) {
        e.setCancelled(true);
    }

    @Override
    public void registerListeners() { }
}
