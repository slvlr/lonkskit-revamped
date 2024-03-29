package me.aiglez.lonkskit.listeners;

import me.aiglez.lonkskit.Constants;
import me.aiglez.lonkskit.KitPlugin;
import me.aiglez.lonkskit.WorldProvider;
import me.aiglez.lonkskit.commands.MainCommand;
import me.aiglez.lonkskit.controllers.Controllers;
import me.aiglez.lonkskit.events.KitSelectEvent;
import me.aiglez.lonkskit.guis.CustomGUI;
import me.aiglez.lonkskit.guis.ShopGUI;
import me.aiglez.lonkskit.messages.Messages;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.struct.HotbarItemStack;
import me.aiglez.lonkskit.struct.ghost.GhostFactory;
import me.aiglez.lonkskit.utils.Logger;
import me.aiglez.lonkskit.utils.MetadataProvider;
import me.aiglez.lonkskit.utils.Various;
import me.libraryaddict.disguise.disguisetypes.watchers.FireballWatcher;
import me.lucko.helper.Schedulers;
import me.lucko.helper.Services;
import me.lucko.helper.metadata.Metadata;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerListeners implements Listener {
    public PlayerListeners(KitPlugin plugin) {
        plugin.registerListener(this);
    }

    // -------------------------------------------- //
    // LOGGING
    // -------------------------------------------- //
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {
        Logger.debug("Handling join of {0}", e.getPlayer().getName());
        LocalPlayer.get(e.getPlayer()).setBukkit(e.getPlayer());
        if (LocalPlayer.get(e.getPlayer()).wasInKP()){
            LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());
            localPlayer.toBukkit().teleport(WorldProvider.KP_WORLD.getSpawnLocation());
            localPlayer.setLastAttacker(null);
            localPlayer.setSelectedKit(null);
            localPlayer.getInventory().clear();
            localPlayer.toBukkit().getActivePotionEffects().forEach(activePe -> localPlayer.toBukkit().removePotionEffect(activePe.getType()));
            localPlayer.toBukkit().getPassengers().clear();
            for (HotbarItemStack hotbarItem : Controllers.PLAYER.getHotbarItems().stream().sorted(Comparator.comparingInt(HotbarItemStack::getOrder)).collect(Collectors.toList())) {
                if (!localPlayer.toBukkit().getInventory().contains(hotbarItem.getItemStack())) {
                    localPlayer.toBukkit().getInventory().addItem(hotbarItem.getItemStack());
                }
            }
        }

    }

    @EventHandler
    public void onClickItem(PlayerInteractEvent e){
        if (Services.load(GhostFactory.class).isGhost(e.getPlayer())) e.setCancelled(true);
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
            LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());
            if (e.hasItem()) {
                        if (Objects.requireNonNull(e.getItem()).hasItemMeta()){
                            if (e.getItem().getItemMeta().hasDisplayName()){
                                if (e.getItem().getItemMeta().getDisplayName().toLowerCase().contains("kit selector") && !e.getItem().getItemMeta().getDisplayName().toLowerCase().contains("custom") ){
                                    if (localPlayer.isValid()) {
                                        if (!Services.load(GhostFactory.class).isGhost(localPlayer.toBukkit())) {
                                            localPlayer.openKitSelector();
                                        }else localPlayer.msg("&3 You should be visible first");
                                    }else {
                                        localPlayer.msg("&3 You should enter the kitpvp world first");
                                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"mail send rangewonk " + e.getPlayer().getDisplayName() + " has kitpvp items in " + e.getPlayer().getWorld().getName());
                                    }
                                }else if (e.getItem().getItemMeta().getDisplayName().toLowerCase().contains("custom kit selector")){
                                     if (localPlayer.isValid()){
                                         if (!Services.load(GhostFactory.class).isGhost(localPlayer.toBukkit())) {
                                             new CustomGUI(localPlayer).open();
                                         }else localPlayer.msg("&3 You should be visible first");
                                     }else {
                                         localPlayer.msg("&3 You should enter the kitpvp world first");
                                         Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"mail send rangewonk " + e.getPlayer().getDisplayName() + " has kitpvp items in " + e.getPlayer().getWorld().getName());
                                     }
                                }
                                else if (e.getItem().hasItemMeta()){
                                    if (e.getItem().getItemMeta().hasDisplayName()){
                                        if (e.getItem().getItemMeta().getDisplayName().toLowerCase().contains("points")){
                                            if (localPlayer.isValid()){
                                                if (!Services.load(GhostFactory.class).isGhost(localPlayer.toBukkit())) {
                                                    new ShopGUI(localPlayer).open();
                                                }else localPlayer.msg("&3 You should be visible first");
                                            }else {
                                                localPlayer.msg("&3 You should enter the kitpvp world first");
                                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"mail send rangewonk " + e.getPlayer().getDisplayName() + " has kitpvp items in " + e.getPlayer().getWorld().getName());
                                            }
                                        }else if (e.getItem().getItemMeta().getDisplayName().toLowerCase().contains("spectator")){
                                            if (localPlayer.isValid()){
                                                if (Services.load(GhostFactory.class).isGhost(e.getPlayer())){
                                                    Services.load(GhostFactory.class).setGhost(e.getPlayer(),false);
                                                    Services.load(GhostFactory.class).removePlayer(e.getPlayer());
                                                    e.getPlayer().setGlowing(!e.getPlayer().isGlowing());
                                                    e.getPlayer().setGliding(!e.getPlayer().isGliding());
                                                }else {
                                                    e.getPlayer().setGlowing(!e.getPlayer().isGlowing());
                                                    e.getPlayer().setGliding(!e.getPlayer().isGliding());
                                                    Services.load(GhostFactory.class).setGhost(e.getPlayer(), true);
                                                    localPlayer.msg("&3You are invisible now");
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                }
            }
        }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLeave(PlayerQuitEvent e) {
        final LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());
        if (localPlayer.isValid()) {
            FeaturesListeners.killstreaks.replace(localPlayer,0);
            localPlayer.setInKP(true);
        }
        if (MainCommand.builders.containsKey(localPlayer)){
            MainCommand.builders.replace(localPlayer,false);
        }
        localPlayer.getMetrics().resetKillStreak();
        localPlayer.setBukkit(null);
        if (Metadata.lookupBlocksWithKey(MetadataProvider.DEMO_BLOCK).containsValue(localPlayer)){
            Metadata.lookupBlocksWithKey(MetadataProvider.DEMO_BLOCK).entrySet().stream()
                    .filter(a -> a.getValue() == localPlayer)
                    .forEach(a -> Schedulers.sync().runLater(() -> {
                        a.getKey().toBlock().setBlockData(Material.AIR.createBlockData());
                        Schedulers.sync().runLater(() -> a.getKey().toBlock().getState().update(true),2L);
                    },3L));
        }
    }

    // -------------------------------------------- //
    // COMBAT TAG
    // -------------------------------------------- //
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onAttack(EntityDamageByEntityEvent e) {
        if(!(e.getEntity() instanceof Player)) return;
        final LocalPlayer victim = LocalPlayer.get((Player) e.getEntity());
        LocalPlayer damager;
        if(e.getDamager() instanceof Player) {
            damager = LocalPlayer.get((Player) e.getDamager());
        } else if(e.getDamager() instanceof Projectile) {
            final ProjectileSource shooter = ((Projectile) e.getDamager()).getShooter();
            if(shooter == null) return;
            if (!(shooter instanceof Player)) return;
            damager = LocalPlayer.get((Player) shooter);
        } else return;

        if(!victim.isValid() || !damager.isValid()) return;

        victim.setLastAttacker(damager);
        damager.setLastAttacker(damager);

        victim.msg("&b[DEBUG] &cYou have entered in combat with {0}.", damager.getLastKnownName());
        damager.msg("&b[DEBUG] &cYou have entered in combat with {0}.", victim.getLastKnownName());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onDeath(PlayerDeathEvent e) {
        final LocalPlayer victim = LocalPlayer.get(e.getEntity());
        if(!victim.isValid()) return;
        if (MainCommand.builders.containsKey(victim)){
            MainCommand.builders.replace(victim,false);
        }
        if (e.getEntity().getKiller() == null ) {
            if (!victim.getLastAttacker().isPresent()){
                Controllers.PLAYER.handleSuicide(victim);
                return;
            }
        }
            e.setShouldDropExperience(false);
        if (e.getEntity().getKiller() != null) Controllers.PLAYER.handleDeathOf(LocalPlayer.get(victim.toBukkit().getKiller()),victim);
        e.setDeathMessage(null);
        victim.setSelectedKit(null);
        e.getDrops().clear();
        for (int i = 0; i < Services.load(KitPlugin.class).getConf().getNode("kill-drops").getInt(); i++ ) {
            e.getEntity().getWorld().dropItem(e.getEntity().getLocation().clone().add(i,0,0), new ItemStack(Material.MUSHROOM_STEW));
        }
        e.getEntity().setBedSpawnLocation(WorldProvider.KP_WORLD.getSpawnLocation());
        victim.setInKP(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRespawn(PlayerRespawnEvent e){
        if (LocalPlayer.get(e.getPlayer()).wasInKP() || LocalPlayer.get(e.getPlayer()).hasSelectedKit()){
            LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());
            e.setRespawnLocation(WorldProvider.KP_WORLD.getSpawnLocation());
            localPlayer.msg(Messages.COMMAND_JOIN_SUCCESSFULLY);
            localPlayer.getInventory().clear();
            localPlayer.setSelectedKit(null);
            localPlayer.setInKP(true);
            for (HotbarItemStack hotbarItem : Controllers.PLAYER.getHotbarItems().stream().sorted(Comparator.comparingInt(HotbarItemStack::getOrder)).collect(Collectors.toList())) {
                if (!localPlayer.toBukkit().getInventory().contains(hotbarItem.getItemStack())) {
                    localPlayer.toBukkit().getInventory().addItem(hotbarItem.getItemStack());
                }
            }
            localPlayer.setInArena(false);
        }
    }

    // -------------------------------------------- //
    // DROP
    // -------------------------------------------- //
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onDrop(PlayerDropItemEvent e) {
        final LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());
        if(!localPlayer.isValid()) return;
        final ItemStack item = e.getItemDrop().getItemStack();

        if(localPlayer.inArena() || !Various.isThrowable(item)) {
            e.setCancelled(true);
            return;
        }

        localPlayer.updateSafeStatus();
    }

    @EventHandler
    public void onKitSelect(KitSelectEvent e){
        if (e.getKit().getDisplayName().toLowerCase().contains("demo")){
            Metadata.provideForPlayer(e.getLocalPlayer().toBukkit()).put(MetadataProvider.DEMOMAN,Boolean.TRUE);
            System.out.println("added");
        }
    }

}
