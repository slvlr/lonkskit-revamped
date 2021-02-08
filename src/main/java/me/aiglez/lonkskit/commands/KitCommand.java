package me.aiglez.lonkskit.commands;

import me.aiglez.lonkskit.KitPlugin;
import me.aiglez.lonkskit.LonksKitProvider;
import me.aiglez.lonkskit.WorldProvider;
import me.aiglez.lonkskit.players.LocalPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 * Handle the kit selector command.
 */
public class KitCommand implements Listener {

    public KitCommand(KitPlugin kitPlugin) {
        kitPlugin.registerListener(this);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        final LocalPlayer localPlayer = LonksKitProvider.getPlayerFactory().getLocalPlayer(e.getPlayer());
        if(e.getMessage().toLowerCase().startsWith("/kits") && localPlayer.isValid()) {
            if (!localPlayer.hasSelectedKit()) {
                if (localPlayer.isValid()) {
                    localPlayer.openKitSelector();
                    e.setCancelled(true);
                }else {
                    localPlayer.msg("&3 You should enter the kitpvp world first");
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"mail send rangewonk " + e.getPlayer().getDisplayName() + " has kitpvp items in " + e.getPlayer().getWorld().getName());
                }
            }else {
                localPlayer.msg("&b[LonksKit] &cYou already have a kit!");
            }
        }

    }
}
