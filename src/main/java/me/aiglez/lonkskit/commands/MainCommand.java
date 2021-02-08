package me.aiglez.lonkskit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import co.aikar.commands.annotation.Optional;
import me.aiglez.lonkskit.LonksKitProvider;
import me.aiglez.lonkskit.WorldProvider;
import me.aiglez.lonkskit.abilities.itembased.DemomanAbility;
import me.aiglez.lonkskit.abilities.itembased.johan.CowboyAbility;
import me.aiglez.lonkskit.controllers.Controllers;
import me.aiglez.lonkskit.messages.Messages;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.struct.HotbarItemStack;
import me.aiglez.lonkskit.utils.Various;
import me.aiglez.lonkskit.utils.items.ItemStackBuilder;
import me.libraryaddict.disguise.DisguiseAPI;
import me.lucko.helper.Schedulers;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Pose;
import org.bukkit.entity.ThrowableProjectile;

import java.util.*;
import java.util.stream.Collectors;

@CommandAlias("%main_command")
public class MainCommand extends BaseCommand {

    // -------------------------------------------- //
    // DEFAULT
    // -------------------------------------------- //
    @Default
    @CommandPermission("lonkskit.kitpvp")
    @Syntax("") @Description("Join the Kit PvP world.")
    public void onDefault(LocalPlayer localPlayer) {
        if(localPlayer.isValid()) {
            localPlayer.msg(Messages.COMMAND_JOIN_ERROR);
            return;
        }
        localPlayer.setSafeStatus(true);

        if (check(localPlayer)){
            localPlayer.msg(Messages.COMMAND_JOIN_UNSAFE);
            return;
        }

        if (!localPlayer.getInventory().isEmpty() && localPlayer.getInventory().getArmorContents().length > 0){
            localPlayer.msg("&4Your inventory must be empty to join the KitPvP world");
            return;
        }
        localPlayer.toBukkit().teleport(WorldProvider.KP_WORLD.getSpawnLocation());
        localPlayer.msg(Messages.COMMAND_JOIN_SUCCESSFULLY);
        localPlayer.getInventory().clear();
        localPlayer.setInKP(true);
        for (HotbarItemStack hotbarItem : Controllers.PLAYER.getHotbarItems().stream().sorted(Comparator.comparingInt(HotbarItemStack::getOrder)).collect(Collectors.toList())) {
            if (!localPlayer.toBukkit().getInventory().contains(hotbarItem.getItemStack())) {
                localPlayer.toBukkit().getInventory().addItem(hotbarItem.getItemStack());
            }
        }
        localPlayer.setInArena(false);
    }

    @HelpCommand
    @Subcommand("help")
    @Syntax("") @Description("Show the help messages.")
    public static void onHelp(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }

    // -------------------------------------------- //
    // STATS
    // -------------------------------------------- //
    @Subcommand("stats")
    @CommandCompletion("@kitpvp_players")
    @Syntax("[target]") @Description("Show your/target's statistics.")
    public void onStats(@Conditions("valid_world") LocalPlayer localPlayer, @Conditions("valid_world") @Flags("other") @Optional LocalPlayer target) {
        if(target == null || target.getUniqueId().equals(localPlayer.getUniqueId())) {
            if(localPlayer.toBukkit().hasPermission("lonkskit.kitpvp.stats")) {
                localPlayer.msg("&eYour statistics:");
                localPlayer.msg("&7Kills: &b{0}", localPlayer.getMetrics().getKillsCount());
                localPlayer.msg("&7Deaths: &b{0}", localPlayer.getMetrics().getDeathsCount());
                localPlayer.msg("&7K/D Ratio: &e{0}", localPlayer.getMetrics().getKDR());
                localPlayer.msg("&7KillStreak: &e{0}", localPlayer.getMetrics().hasKillStreak() ? "" + localPlayer.getMetrics().getKillStreak() : "&cNone");
                localPlayer.msg("&7Points: &e{0}", localPlayer.getPoints());
            } else {
                localPlayer.msg(Messages.COMMAND_ENGINE_PERMISSION_DENIED);
            }
        } else {
            if(localPlayer.toBukkit().hasPermission("lonkskit.kitpvp.stats.other")) {
                localPlayer.msg("&e{0}'s statistics:", target.getLastKnownName());
                localPlayer.msg("&7Kills: &b{0}", target.getMetrics().getKillsCount());
                localPlayer.msg("&7Deaths: &b{0}", target.getMetrics().getDeathsCount());
                localPlayer.msg("&7K/D Ratio: &e{0}", target.getMetrics().getKDR());
                localPlayer.msg("&7KillStreak: &e{0}", target.getMetrics().hasKillStreak() ? "" + target.getMetrics().getKillStreak() : "&cNone");
                localPlayer.msg("&7Points: &e{0}", target.getPoints());
            } else {
                localPlayer.msg(Messages.COMMAND_ENGINE_PERMISSION_DENIED);
            }
        }
    }


    //----------------------------------------------//
    //BUILD COMMAND                                 //
    //----------------------------------------------//
    public static final Map<LocalPlayer,Boolean> builders = new HashMap<>();
    @Subcommand("build")
    @CommandCompletion("@kitpvp_players")
    @Syntax("[target][why]") @Description("toggle on/off build and break blocks") @CommandPermission("Kitpvp.build")
    public void onBuild(LocalPlayer localPlayer,@Conditions("valid_world")@Flags("other") @Optional LocalPlayer target,@Optional String why){
        if (target == null || target.getUniqueId().equals(localPlayer.getUniqueId())){
            if (builders.containsKey(localPlayer)){
                if (builders.get(localPlayer)){

                    builders.replace(localPlayer,false);
                    localPlayer.msg("&cYou have disabled your kitpvp building mode.");
                }else {
                    builders.replace(localPlayer, true);
                    localPlayer.msg("&aYou have enabled your kitpvp building mode.");
                }
            }else {
                builders.put(localPlayer,true);
                localPlayer.msg("&aYou have enabled your kitpvp building mode.");
            }
        }
        else if (target.isValid()){
                if (builders.containsKey(target)){
                    if (builders.get(target)){
                        builders.replace(target,false);
                        localPlayer.msg("&cYou have disabled building mode &6" + target.getLastKnownName());
                        target.msg("&c" + localPlayer.getLastKnownName() + "&c has disabled your kitpvp building mode");

                    }else {
                        builders.replace(target,true);
                        localPlayer.msg("&aYou have enabled kitpvp building mode for &6" + target.getLastKnownName());
                        target.msg("&a" + localPlayer.getLastKnownName() + "&a has enabled your kitpvp building mode.");
                    }
                }else {
                    builders.put(target,true);
                    localPlayer.msg("&aYou have enabled kitpvp building mode for &6" + target.getLastKnownName());
                    target.msg("&a" + localPlayer.getLastKnownName() + "&a has enabled your kitpvp building mode");

                }
        }else localPlayer.msg("The player is not in KPWORLD");




    }
    @Subcommand("setspawn")
    @Syntax("") @Description("Set the spawn point") @CommandPermission("lonkskit.admin.spawn")
    public void onsetPoint(LocalPlayer localPlayer){
        if (localPlayer.getLocation().getWorld() == WorldProvider.KP_WORLD){
            WorldProvider.KP_WORLD.setSpawnLocation(localPlayer.getLocation());
            localPlayer.msg("&3You Set up the location successfully");
        }
    }
    // -------------------------------------------- //
    // CLEAR KIT
    // -------------------------------------------- //
    @CommandAlias("clearkit|ck") @Subcommand("clearkit|ck")
    @CommandCompletion("@kitpvp_players")
    @Syntax("[target]") @Description("Clear your or target's selected kit.")
    public void onClearKit(@Conditions("valid_world") LocalPlayer localPlayer, @Conditions("valid_world") @Flags("other") @Optional LocalPlayer target) {
        if(target == null) {
            if(localPlayer.toBukkit().hasPermission("lonkskit.clearkit")) {
                if(!localPlayer.isSafe()) {
                    localPlayer.msg("&cYou can't clear your kit! You have items in your inventory");
                    return;

                }
                if (check(localPlayer)){
                    localPlayer.msg("&4You can't clear kit cause you have a 'Throwable' item");
                    return;
                }
                DisguiseAPI.undisguiseToAll(localPlayer.toBukkit());
                if (CowboyAbility.cowboys.containsKey(localPlayer)){
                    CowboyAbility.cowboys.entrySet().stream().filter(a -> a.getKey() == localPlayer).findAny().ifPresent(x -> {
                        x.getValue().setHealth(0);
                        CowboyAbility.cowboys.remove(x.getKey());
                    });
                }
                localPlayer.setSelectedKit(null);
                localPlayer.getInventory().clear();
                localPlayer.toBukkit().getActivePotionEffects().forEach(activePe -> localPlayer.toBukkit().removePotionEffect(activePe.getType()));
                for (HotbarItemStack hotbarItem : Controllers.PLAYER.getHotbarItems().stream().sorted(Comparator.comparingInt(HotbarItemStack::getOrder)).collect(Collectors.toList())) {
                    if (!localPlayer.toBukkit().getInventory().contains(hotbarItem.getItemStack())) {
                        localPlayer.toBukkit().getInventory().addItem(hotbarItem.getItemStack());
                    }
                }
                localPlayer.msg("&cYou have cleared your kit.");
            } else {
                localPlayer.msg(Messages.COMMAND_ENGINE_PERMISSION_DENIED);
            }
        } else {
            if(localPlayer.toBukkit().hasPermission("lonkskit.clearkit.other")) {
                if(!target.isSafe()) {
                    localPlayer.msg("&cYou can't clear his kit! He has items in his inventory");
                    return;
                }
                if (check(target)){
                    localPlayer.msg("&4You can't clear this kit cause he have a 'Throwable' item");
                    return;
                }
                target.setSelectedKit(null);
                target.getInventory().clear();
                target.toBukkit().getActivePotionEffects().forEach(activePe -> target.toBukkit().removePotionEffect(activePe.getType()));
                for (HotbarItemStack hotbarItem : Controllers.PLAYER.getHotbarItems().stream().sorted(Comparator.comparingInt(HotbarItemStack::getOrder)).collect(Collectors.toList())) {
                    if (!localPlayer.toBukkit().getInventory().contains(hotbarItem.getItemStack())) {
                        localPlayer.toBukkit().getInventory().addItem(hotbarItem.getItemStack());
                    }
                }                localPlayer.msg("&cYou have cleared {0}'s kit.", target.getLastKnownName());
                target.msg("&eYour kit has been cleared by an admin.");
            } else {
                localPlayer.msg(Messages.COMMAND_ENGINE_PERMISSION_DENIED);
            }
        }
    }

    // -------------------------------------------- //
    // CLEAR KIT
    // -------------------------------------------- //
    @CommandAlias("clearcooldown|cc") @Subcommand("clearcooldown|cc")
    @CommandCompletion("@kitpvp_players")
    @Syntax("[target]") @Description("Clear your or target's cooldowns.")
    public void onClearCooldown(@Conditions("valid_world") LocalPlayer localPlayer, @Conditions("valid_world") @Flags("other") @Optional LocalPlayer target) {
        if(target == null) {
            if(localPlayer.toBukkit().hasPermission("lonkskit.clearcooldown")) {
                clearCooldown(localPlayer);
                localPlayer.msg("&b[LonksKit] &cYou have cleared your cooldowns.");

            } else {
                localPlayer.msg(Messages.COMMAND_ENGINE_PERMISSION_DENIED);
            }
        } else {
            if(localPlayer.toBukkit().hasPermission("lonkskit.clearcooldown.other")) {
                clearCooldown(target);
                localPlayer.msg("&b[LonksKit] &cYou have cleared {0}'s cooldowns.", target.getLastKnownName());

                target.msg("&b[LonksKit] &eYour cooldowns have been cleared by an admin.");
            } else {
                localPlayer.msg(Messages.COMMAND_ENGINE_PERMISSION_DENIED);
            }
        }
    }


    private void clearCooldown(LocalPlayer localPlayer) {
        LonksKitProvider.getAbilityFactory().getAbilities()
                .forEach(ability -> ability.getCooldown().setLastTested(localPlayer, (System.currentTimeMillis() - ability.getCooldown().getBase().getTimeout())));
    }
    public static boolean check(LocalPlayer localPlayer){
        return Arrays.stream(localPlayer.getInventory().getContents())
                .filter(Objects::nonNull)
                .anyMatch(Various::isThrowable);
    }
}
