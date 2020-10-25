package me.aiglez.lonkskit.listeners;

import me.aiglez.lonkskit.KitPlugin;
import me.aiglez.lonkskit.controllers.Controllers;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.Various;
import me.aiglez.lonkskit.utils.items.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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
        final LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());
        /*
        localPlayer.getLastAttacker().ifPresent(damager -> {
            Controllers.PLAYER.handleDeathMessage(damager, localPlayer);
            localPlayer.toBukkit().setHealth(0D);

        });
         */
        localPlayer.setBukkit(null);
    }

    // -------------------------------------------- //
    // COMBAT TAG
    // -------------------------------------------- //
    @EventHandler
    public void onAttack(EntityDamageByEntityEvent e) {
        if(!(e.getEntity() instanceof Player || e.getDamager() instanceof Player)) return;
        final LocalPlayer victim = LocalPlayer.get((Player) e.getEntity());
        final LocalPlayer damager = LocalPlayer.get((Player) e.getDamager());
        if(!victim.isValid() || !damager.isValid()) return;

        victim.setLastAttacker(damager);
        damager.setLastAttacker(damager);

        victim.msg("&b[LonksKit] &cYou have entered in combat with {0}.", damager.getLastKnownName());
        damager.msg("&b[LonksKit] &cYou have entered in combat with {0}.", victim.getLastKnownName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(PlayerDeathEvent e) {
        final LocalPlayer victim = LocalPlayer.get(e.getEntity());
        victim.getLastAttacker().ifPresent(killer -> Controllers.PLAYER.handleDeathOf(killer, victim));

        e.setShouldDropExperience(false);
        e.setDeathMessage(null);

        e.getDrops().clear();
        e.getDrops().add(ItemStackBuilder.of(Material.MUSHROOM_STEW).amount(5).build());
    }

    // -------------------------------------------- //
    // DROP
    // -------------------------------------------- //
    //@EventHandler(priority = EventPriority.HIGHEST)
    public void onDrop(PlayerDropItemEvent e) {
        final LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());
        if(!localPlayer.isValid()) return;
        final ItemStack item = e.getItemDrop().getItemStack();

        if(localPlayer.inArena()) {
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
