package me.aiglez.lonkskit.listeners;

import me.aiglez.lonkskit.KitPlugin;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.Various;
import me.aiglez.lonkskit.utils.items.ItemStackNBT;
import me.lucko.helper.Services;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.*;
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
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInventoryMove(InventoryClickEvent e) {

        if(!(e.getWhoClicked() instanceof Player) || e.getClickedInventory() == null) return;
        final LocalPlayer localPlayer = LocalPlayer.get((Player) e.getWhoClicked());
        if(!localPlayer.isValid() || !(e.getClickedInventory() instanceof PlayerInventory)) return;
        if (e.getInventory().getType() == InventoryType.ENDER_CHEST){
            if (e.getCurrentItem() != null){
                if (!Various.isThrowable(e.getCurrentItem())){
                    e.setCancelled(true);
                    return;
                }
            }
        }
            if (e.getInventory().getType() == InventoryType.ENDER_CHEST || e.getInventory().getType() == InventoryType.CHEST) {
                if (e.getCurrentItem() != null && isHotbarSigned(e.getCurrentItem())) {
                    e.setResult(Event.Result.DENY);
                    e.setCancelled(true);
                    return;
                }

                if (e.getCursor() != null && isHotbarSigned(e.getCursor())) {
                    e.setResult(Event.Result.DENY);
                    e.setCancelled(true);
                    return;
                }

                final ItemStack hotbar = (e.getClick() == org.bukkit.event.inventory.ClickType.NUMBER_KEY) ? e.getWhoClicked().getInventory().getItem(e.getHotbarButton()) : e.getCurrentItem();
                if (hotbar != null && isHotbarSigned(hotbar)) {
                    e.setResult(Event.Result.DENY);
                    e.setCancelled(true);
                }
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
            return;
        }

        if(e.getCursor() != null && isHotbarSigned(e.getCursor())) {
            e.setResult(Event.Result.DENY);
            e.setCancelled(true);
            return;
        }


    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSoupHealing(PlayerInteractEvent event) {
        if (event.getItem() != null && event.getItem().getType() != Material.AIR) {
            if (event.getItem().getType() == Material.MUSHROOM_STEW && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
                if (event.getPlayer().getHealth() == 20D) {
                    event.getPlayer().sendMessage("[Debug] You're full HP");
                } else if (event.getPlayer().getHealth() + Services.load(KitPlugin.class).getConf().getNode("soup-healing").getInt() <= 20.0D) {
                    event.getPlayer().setHealth(event.getPlayer().getHealth() + (double) Services.load(KitPlugin.class).getConf().getNode("soup-healing").getInt());
                    event.getPlayer().sendMessage("[Debug] Added " + Services.load(KitPlugin.class).getConf().getNode("soup-healing").getInt());
                    event.getPlayer().getInventory().setItemInMainHand(null);
                } else {
                    event.getPlayer().setHealth(20.0D);
                    event.getPlayer().getInventory().setItemInMainHand(null);
                }
            }
        }
    }

    private boolean isHotbarSigned(ItemStack item) {
        return ItemStackNBT.hasKey(item, "logging-item");
    }
}
