package me.aiglez.lonkskit.listeners;

import me.aiglez.lonkskit.KitPlugin;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.Various;
import me.aiglez.lonkskit.utils.items.ItemStackNBT;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;

public class SafetyListeners implements Listener {

    public SafetyListeners(KitPlugin plugin) {
        plugin.registerListener(this);
    }

    // -------------------------------------------- //
    // BLOCK COMMANDS
    // -------------------------------------------- //
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        final LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());
        if(!localPlayer.isValid()) return;

        if(!localPlayer.isSafe()) {
            unsafeReminder(localPlayer);
            e.setCancelled(true);
        }
    }

    // --------------------------------------------------------------- //
    // BLOCK ENDERCHEST (block putting non-throwable items in the ec)
    // --------------------------------------------------------------- //
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if(!(e.getWhoClicked() instanceof Player) || e.getClickedInventory() == null || e.getClickedInventory().getType() != InventoryType.ENDER_CHEST) return;
        final LocalPlayer localPlayer = LocalPlayer.get((Player) e.getWhoClicked());
        final ItemStack moving = (e.getClick() == ClickType.NUMBER_KEY) ? localPlayer.getInventory().getItem(e.getHotbarButton()) : e.getCursor();
        if(moving == null) return;

        if(ItemStackNBT.hasKey(moving, "kitpvp")) {
            localPlayer.msg("&b[LonksKit] &cYou can't put Kit PvP items in your enderchest.");
            e.setCancelled(true);
        } else {
            if(!Various.isThrowable(moving)) {
                e.setCancelled(true);
            }
        }
    }


    private void unsafeReminder(LocalPlayer localPlayer) {
        localPlayer.msg("&b[LonksKit] &cYou need to throw your items or put them in your enderchest, to perform this action.");
    }
}
