package me.aiglez.lonkskit.commands;

import me.aiglez.lonkskit.KitPlugin;
import me.aiglez.lonkskit.LonksKitProvider;
import me.aiglez.lonkskit.players.LocalPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class KitCommand implements Listener {

    public KitCommand(KitPlugin kitPlugin) {
        kitPlugin.registerListener(this);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        final LocalPlayer localPlayer = LonksKitProvider.getPlayerFactory().getLocalPlayer(e.getPlayer());
        if(e.getMessage().toLowerCase().startsWith("/kits")) {
            localPlayer.openKitSelector();
            e.setCancelled(true);
        }
    }
}
