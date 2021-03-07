package me.aiglez.lonkskit.listeners;

import me.aiglez.lonkskit.KitPlugin;
import me.aiglez.lonkskit.commands.MainCommand;
import me.aiglez.lonkskit.kits.Kit;
import me.aiglez.lonkskit.kits.KitFactory;
import me.aiglez.lonkskit.messages.Messages;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.Various;
import me.aiglez.lonkskit.utils.items.ItemStackNBT;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

public class SafetyListeners implements Listener {

    public SafetyListeners(KitPlugin plugin) {
        plugin.registerListener(this);
    }

    // -------------------------------------------- //
    // BLOCK TELEPORTING BETWEEN WORLDS
    // -------------------------------------------- //
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onTeleport(PlayerTeleportEvent e) {
        final LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());
        final Location from = e.getFrom(), to = e.getTo();

        if(localPlayer.isValid() && localPlayer.hasSelectedKit()) {
            localPlayer.msg("&b[LonksKit] &cYou can't teleport you have selected a kit.");
            e.setCancelled(true);
        }

    }


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
        if(!(e.getWhoClicked() instanceof Player) || e.getClickedInventory() == null || e.getClickedInventory().getType() != InventoryType.ENDER_CHEST || e.getClickedInventory().getType() != InventoryType.CHEST ) return;
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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryCli2ck(InventoryClickEvent e){
        LocalPlayer localPlayer = LocalPlayer.get((Player) e.getWhoClicked());
        if (e.getView().getTitle().toLowerCase().contains("custom")){
            e.setCancelled(true);
            if (e.getCurrentItem() != null){
                Kit kit = KitFactory.make().getKitByItem(e.getCurrentItem());
                if (kit != null && !MainCommand.check(localPlayer)){
                    localPlayer.msg("kit != null");
                    if (!localPlayer.hasSelectedKit()) {
                        if (localPlayer.isValid()) {
                            localPlayer.setSelectedKit(kit);
                            localPlayer.msg(Messages.SELECTOR_SELECTED, kit.getDisplayName());
                            e.getWhoClicked().closeInventory();
                        }else localPlayer.msg("&3you should join the kitpvp world to select a kit");
                    }else localPlayer.msg("&3clear your kit then choose another one");
                }else localPlayer.msg("&4You can't choose a kit cause you have a 'Throwable' item");
            }
        }
    }


    private void unsafeReminder(LocalPlayer localPlayer) {
        localPlayer.msg("&b[LonksKit] &cYou need to throw your items or put them in your enderchest, to perform this action.");
    }
}
