package me.aiglez.lonkskit.listeners;

import me.aiglez.lonkskit.KitPlugin;
import me.aiglez.lonkskit.LonksKitProvider;
import me.aiglez.lonkskit.abilities.Ability;
import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class AbilityListeners implements Listener {

    public AbilityListeners(KitPlugin plugin) {
        plugin.registerListener(this);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        final LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());
        if(!localPlayer.isValid()) return;

        // handle abilities
        if(!e.hasItem()) return;
        final ItemStack item = e.getItem();

        if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            for (final Ability ability : LonksKitProvider.getAbilityFactory().getAbilities()) {
                if(ability instanceof ItemStackAbility) {
                    final ItemStackAbility itemAbility = (ItemStackAbility) ability;
                    if(itemAbility.isItemStack(item)) {
                        itemAbility.whenRightClicked(e);
                        return;
                    }
                }
            }
        } else if(e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
            for (final Ability ability : LonksKitProvider.getAbilityFactory().getAbilities()) {
                if(ability instanceof ItemStackAbility) {
                    final ItemStackAbility itemAbility = (ItemStackAbility) ability;
                    if(itemAbility.isItemStack(item)) {
                        itemAbility.whenLeftClicked(e);
                        return;
                    }
                }
            }
        }
    }

        /*

    @EventHandler
    public void onDamage(PlayerItemDamageEvent e) {
        final ItemStack item = e.getItem();
        for (final Ability ability : LonksKitProvider.getAbilityFactory().getAbilities()) {
            if(ability instanceof ItemStackAbility) {
                final ItemStackAbility itemAbility = (ItemStackAbility) ability;
                if(itemAbility.isItemStack(item)) {
                    e.setCancelled(true);
                    return;
                }
            }
        }

    }

     */

}