package me.aiglez.lonkskit.listeners;

import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import me.aiglez.lonkskit.KitPlugin;
import me.aiglez.lonkskit.WorldProvider;
import me.aiglez.lonkskit.abilities.Ability;
import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.abilities.factory.AbilityFactoryImpl;
import me.aiglez.lonkskit.abilities.itembased.DemomanAbility;
import me.aiglez.lonkskit.commands.MainCommand;
import me.aiglez.lonkskit.messages.Replaceable;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.Various;
import me.lucko.helper.Schedulers;
import me.lucko.helper.config.objectmapping.ObjectMappingException;
import me.lucko.helper.cooldown.Cooldown;
import me.lucko.helper.cooldown.CooldownMap;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class FeaturesListeners implements Listener {
    public FeaturesListeners(KitPlugin plugin){plugin.registerListener(this);}
    public static final Material teleM = Material.matchMaterial(AbilityFactoryImpl.getFileByName("tele").getNode("item", "material").getString("STONE"));
    private static final CooldownMap<LocalPlayer> cooldown = CooldownMap.create(Cooldown.of(AbilityFactoryImpl.getFileByName("tele").getNode("cooldown").getInt(10),TimeUnit.SECONDS));
    public static final Map<Block,LocalPlayer> demoBlocks = new HashMap<>();
    public static final Map<LocalPlayer,Integer> killstreaks = Maps.newHashMap();
    //----------------------------------------//
    //     BUILD/BREAK BLOCKS                 //
    //----------------------------------------//
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlaceBlock(BlockPlaceEvent e){
            if (e.getPlayer().getWorld() == WorldProvider.KP_WORLD) {
                if (ItemStackAbility.ABILITY_ITEMS.contains(e.getBlock().getType())) {
                    if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
                        if (e.getBlock().getType() == teleM && LocalPlayer.get(e.getPlayer()).hasSelectedKit()) {
                            e.setCancelled(true);
                            if (!cooldown.test(LocalPlayer.get(e.getPlayer()))) {
                                LocalPlayer.get(e.getPlayer()).msg("&b[LonksKit] &cPlease wait, {0} second(s) left", cooldown.remainingTime(LocalPlayer.get(e.getPlayer()), TimeUnit.SECONDS));
                                e.setCancelled(true);
                                return;
                            } else {
                                double x = AbilityFactoryImpl.getFileByName("tele").getNode("radius", "x-axis").getInt();
                                double y = AbilityFactoryImpl.getFileByName("tele").getNode("radius", "y-axis").getInt();
                                double z = AbilityFactoryImpl.getFileByName("tele").getNode("radius", "z-axis").getInt();
                                e.getPlayer().getWorld().getNearbyPlayers(e.getBlock().getLocation(), x, y, z, player -> {
                                    if (!LocalPlayer.get(player).hasSelectedKit()) {
                                        player.teleport(e.getBlock().getLocation().clone().add(0.5, 1, 0.5));
                                    } else if (!LocalPlayer.get(player).getNullableSelectedKit().getBackendName().equalsIgnoreCase(LocalPlayer.get(e.getPlayer()).getNullableSelectedKit().getBackendName())) {
                                        player.teleport(e.getBlock().getLocation().clone().add(0.5, 1, 0.5));
                                    }
                                    return true;
                                });
                            }
                        }
                        if (e.getBlock().getType() == Material.STONE_PRESSURE_PLATE && LocalPlayer.get(e.getPlayer()).hasSelectedKit()) {
                            if (LocalPlayer.get(e.getPlayer()).getNullableSelectedKit().hasAbility(DemomanAbility.INSTANCE)) {
                                System.out.println(e.getBlockAgainst().getType());
                                if (Various.assertNotSurroundedWithCactus(e.getBlock()) && DemomanAbility.getInterdit().contains(e.getBlockAgainst().getType())) {
                                    demoBlocks.put(e.getBlock(),LocalPlayer.get(e.getPlayer()));
                                    e.getPlayer().sendMessage("block added to the list");
                                }else e.setCancelled(true);
                            }else e.setCancelled(true);
                        }else {
                            e.getPlayer().sendMessage(ChatColor.BOLD + "" + ChatColor.DARK_AQUA + "You Can't Place Ability Items");
                            e.setCancelled(true);
                        }
                    }else {
                        LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());
                        if (MainCommand.builders.containsKey(localPlayer)){
                            if (!MainCommand.builders.get(localPlayer)) e.setCancelled(true);
                        }else e.setCancelled(true);
                    }

                    return;
                }
                LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());
                if (!MainCommand.builders.containsKey(localPlayer)) {
                    if (e.getBlock().getType() != Material.STONE_PRESSURE_PLATE) {
                        e.setCancelled(true);
                        e.getPlayer().sendMessage(ChatColor.BOLD + "" + ChatColor.DARK_AQUA + "You don't have permission to build blocks");
                        return;
                    }else {
                        if (Various.assertNotSurroundedWithCactus(e.getBlock())) {
                            demoBlocks.put(e.getBlock(),LocalPlayer.get(e.getPlayer()));
                            e.getPlayer().sendMessage("block added to the list");
                        }
                        else e.setCancelled(true);
                    }
                }
                if (!MainCommand.builders.get(localPlayer)) {
                    if (e.getBlock().getType() != Material.STONE_PRESSURE_PLATE) {
                        e.setCancelled(true);
                        e.getPlayer().sendMessage(ChatColor.BOLD + "" + ChatColor.DARK_AQUA + "You don't have permission to build blocks");
                        return;
                    }else {
                        if (Various.assertNotSurroundedWithCactus(e.getBlock())) {
                            demoBlocks.put(e.getBlock(),LocalPlayer.get(e.getPlayer()));
                            e.getPlayer().sendMessage("block added to the list");
                        }
                        else e.setCancelled(true);
                    }
                }
                if (ItemStackAbility.ABILITY_ITEMS.contains(e.getBlock().getType())) {
                    if (e.getBlock().getType() != Material.STONE_PRESSURE_PLATE) {
                        e.setCancelled(true);
                        e.getPlayer().sendMessage(ChatColor.BOLD + "" + ChatColor.DARK_AQUA + "You Can't Place Ability Items");
                    }else {
                        if (localPlayer.getNullableSelectedKit().hasAbility(DemomanAbility.INSTANCE)){
                            if (Various.assertNotSurroundedWithCactus(e.getBlock()) && DemomanAbility.getInterdit().contains(e.getBlockAgainst().getType())) {
                                demoBlocks.put(e.getBlock(),LocalPlayer.get(e.getPlayer()));
                                e.getPlayer().sendMessage("block added to the list");
                            }
                            else e.setCancelled(true);
                        }else e.setCancelled(true);
                    }
                    }
                }

        }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBreakBlock(BlockBreakEvent e){
            if (e.getPlayer().getWorld() == WorldProvider.KP_WORLD) {
                LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());
                if (!MainCommand.builders.containsKey(localPlayer)) {
                    e.setCancelled(true);
                    e.getPlayer().sendMessage(ChatColor.BOLD + "" + ChatColor.DARK_AQUA + "You don't have permission to break blocks");
                    return;
                }
                if (!MainCommand.builders.get(localPlayer)) {
                    e.setCancelled(true);
                    e.getPlayer().sendMessage(ChatColor.BOLD + "" + ChatColor.DARK_AQUA + "You don't have permission to break blocks");
                    return;
                }
                if (ItemStackAbility.ABILITY_ITEMS.contains(e.getBlock().getType())) {
                    if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
                        e.setCancelled(true);
                        e.getPlayer().sendMessage(ChatColor.BOLD + "" + ChatColor.DARK_AQUA + "You Can't Break Ability Items");
                    } else if (e.getPlayer().getGameMode() == GameMode.CREATIVE) {
                        if (!MainCommand.builders.get(localPlayer)){
                            e.setCancelled(true);
                            e.getPlayer().sendMessage(ChatColor.BOLD + "" + ChatColor.DARK_AQUA + "You don't have permission to break blocks");
                        }
                    }

                }
            }

    }
    //********************************************//
    //---------------KILL STREAKS-----------------//
    //********************************************//


    @EventHandler(priority = EventPriority.HIGH)
    public void onKillEvent(PlayerDeathEvent e){
        if (e.getEntity().getKiller() != null){
            if (e.getEntity().getWorld() == WorldProvider.KP_WORLD) {
                LocalPlayer killer = LocalPlayer.get(e.getEntity().getKiller());
                LocalPlayer victim = LocalPlayer.get(e.getEntity());
                if (killstreaks.containsKey(killer)) {
                    if ((killstreaks.get(killer) + 1)  % 5 == 0 && killstreaks.get(killer) != null) {
                        WorldProvider.KP_WORLD.getPlayers().forEach(player -> player.sendMessage(ChatColor.translateAlternateColorCodes('&',Replaceable.handle("&6[&bLonksKits&6]&a {0} got a killstreak of {1}!", killer.getLastKnownName(), killstreaks.get(killer) + 1))));
                    }
                } else {
                    killstreaks.put(killer, 0);
                }
                if (killstreaks.containsKey(victim)){
                    if (killstreaks.get(victim) >= 5){
                        WorldProvider.KP_WORLD.getPlayers().forEach(player -> player.sendMessage(ChatColor.translateAlternateColorCodes('&',Replaceable.handle("&7{0} &aended &c{1}'s &akillstreak of {2} using &b{3}!",killer.getLastKnownName(),victim.getLastKnownName(),killstreaks.get(victim),killer.getNullableSelectedKit() == null ? "Without Kit" : killer.getNullableSelectedKit().getDisplayName()))));
                        killstreaks.replace(victim,0);
                    }
                }else {
                    killstreaks.put(victim,0);
                }
                killstreaks.replace(killer,killstreaks.get(killer) + 1);
                killstreaks.replace(victim,0);
                if (demoBlocks.containsValue(victim)){
                    FeaturesListeners.demoBlocks.entrySet().stream().filter(a -> a.getValue() == victim).forEach(block -> Schedulers.sync().runLater(() -> {
                        block.getKey().setBlockData(Material.AIR.createBlockData());
                        Schedulers.sync().runLater(() -> block.getKey().getState().update(true),2L);
                    },3L));
                }
            }
        }
    }

}
