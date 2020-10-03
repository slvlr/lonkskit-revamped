package me.aiglez.lonkskit.listeners;

import me.aiglez.lonkskit.KitPlugin;
import me.aiglez.lonkskit.LonksKitProvider;
import me.aiglez.lonkskit.abilities.Ability;
import me.aiglez.lonkskit.abilities.ItemStackAbility;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;

public class AbilityListeners implements Listener {

    public AbilityListeners(KitPlugin plugin) {
        plugin.registerListener(this);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        // handle abilities
        if(e.hasItem() && (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            final ItemStack item = e.getItem();
            for (final Ability ability : LonksKitProvider.getAbilityFactory().getAbilities()) {
                if(ability instanceof ItemStackAbility) {
                    final ItemStackAbility itemAbility = (ItemStackAbility) ability;
                    if(itemAbility.isItemStack(item)) {
                        itemAbility.whenClicked(e);
                        return;
                    }
                }
            }
        }
    }

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
}
