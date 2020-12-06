package me.aiglez.lonkskit.listeners;

import me.aiglez.lonkskit.KitPlugin;
import me.aiglez.lonkskit.controllers.Controllers;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.struct.HotbarItemStack;
import me.aiglez.lonkskit.utils.items.ItemStackNBT;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class InteractListeners implements Listener {

    public InteractListeners(KitPlugin plugin) {
        plugin.registerListener(this);
    }

    // -------------------------------------------- //
    // BLOCK ITEM MOVING
    // -------------------------------------------- //
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryMove(InventoryClickEvent e) {
        if(!(e.getWhoClicked() instanceof Player) || e.getClickedInventory() == null) return;
        final LocalPlayer localPlayer = LocalPlayer.get((Player) e.getWhoClicked());
        if(!localPlayer.isValid() || !(e.getClickedInventory() instanceof PlayerInventory)) return;

        if(e.getCurrentItem() != null && isHotbarSigned(e.getCurrentItem())) {
            e.setResult(Event.Result.DENY);
            e.setCancelled(true);
            return;
        }

        if(e.getCursor() != null && isHotbarSigned(e.getCursor())) {
            e.setResult(Event.Result.DENY);
            e.setCancelled(true);
            return;
        }

        final ItemStack hotbar = (e.getClick() == org.bukkit.event.inventory.ClickType.NUMBER_KEY) ? e.getWhoClicked().getInventory().getItem(e.getHotbarButton()) : e.getCurrentItem();
        if(hotbar != null && isHotbarSigned(hotbar)) {
            e.setResult(Event.Result.DENY);
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryDrag(InventoryDragEvent e) {
        if(!(e.getWhoClicked() instanceof Player)) return;
        final LocalPlayer localPlayer = LocalPlayer.get((Player) e.getWhoClicked());
        if(!localPlayer.isValid() || !(e.getInventory() instanceof PlayerInventory)) return;

        if(isHotbarSigned(e.getOldCursor())) {
            e.setResult(Event.Result.DENY);
            e.setCancelled(true);
        }

        if(e.getCursor() != null && isHotbarSigned(e.getCursor())) {
            e.setResult(Event.Result.DENY);
            e.setCancelled(true);
        }


    }

    // -------------------------------------------- //
    // HOTBAR ITEMS
    // -------------------------------------------- //
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent e) {
        final LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());
        if(!localPlayer.isValid() || !e.hasItem()) return;
        final ItemStack item = e.getItem();

        if(isHotbarSigned(item)) {
            for (HotbarItemStack hotbarItem : Controllers.PLAYER.getHotbarItems()) {
                if(hotbarItem.getItemStack().isSimilar(item)) {
                    localPlayer.msg("You clicked on a hotbar item");
                    hotbarItem.runCommands(localPlayer);
                    return;
                }
            }
            e.setCancelled(true);
        }

    }

    private boolean isHotbarSigned(ItemStack item) {
        return ItemStackNBT.hasKey(item, "logging-item");
    }
}
