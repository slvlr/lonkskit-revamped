package me.aiglez.lonkskit.abilities.itembased;

import lombok.Getter;
import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.abilities.helpers.ConstructHelper;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.lucko.helper.config.yaml.YAMLConfigurationLoader;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DomeBuilderAbility extends ItemStackAbility {

    public DomeBuilderAbility(YAMLConfigurationLoader configurationLoader) throws IOException {
        super("domebuilder", configurationLoader);
    }

    @Override
    public void whenRightClicked(PlayerInteractEvent e) {
        e.setCancelled(true);
        final LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());

        final boolean result = ConstructHelper.buildDome(localPlayer.getLocation(), 300);
        if(result) {
            localPlayer.msg("&a(DomeBuilder - Debug) &aSuccessfully built a dome!");
        } else {
            localPlayer.msg("&a(DomeBuilder - Debug) &cYou can't build a dome there!");
        }
    }

    @Override
    public void whenLeftClicked(PlayerInteractEvent e) {
        e.setCancelled(true);
    }

    @Override
    public void registerListeners() { }

}
