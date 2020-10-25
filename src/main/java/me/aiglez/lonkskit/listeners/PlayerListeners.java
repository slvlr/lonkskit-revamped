package me.aiglez.lonkskit.listeners;

import me.aiglez.lonkskit.KitPlugin;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.Various;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerListeners implements Listener {

    public PlayerListeners(KitPlugin plugin) {
        plugin.registerListener(this);
    }

    // -------------------------------------------- //
    // LOGGING
    // -------------------------------------------- //
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {
        LocalPlayer.get(e.getPlayer()).setBukkit(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLeave(PlayerQuitEvent e) {
        LocalPlayer.get(e.getPlayer()).setBukkit(null);
    }

    // -------------------------------------------- //
    // DEATH
    // -------------------------------------------- //
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(PlayerDeathEvent e) {
        final LocalPlayer victim = LocalPlayer.get(e.getEntity());
        if(!victim.inKPWorld() || victim.toBukkit().getKiller() == null) return;


    }

    // -------------------------------------------- //
    // DROP
    // -------------------------------------------- //
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDrop(PlayerDropItemEvent e) {
        final LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());
        if(!localPlayer.inKPWorld()) return;
        final ItemStack item = e.getItemDrop().getItemStack();

        if(localPlayer.atArena()) {
            localPlayer.msg("&b[LonksKit] &cYou can't throw anything in the arena!");
            e.setCancelled(true);
            return;
        }

        if(!Various.isThrowable(item)) {
            localPlayer.msg("&b[LonksKit] &cYou can't throw that item.");
            e.setCancelled(true);
        } else {
            localPlayer.updateSafeStatus();
        }

    }
}
