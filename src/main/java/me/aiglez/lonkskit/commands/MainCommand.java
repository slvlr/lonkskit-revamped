package me.aiglez.lonkskit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import me.aiglez.lonkskit.LonksKitProvider;
import me.aiglez.lonkskit.WorldProvider;
import me.aiglez.lonkskit.controllers.Controllers;
import me.aiglez.lonkskit.messages.Messages;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.struct.HotbarItemStack;
import org.bukkit.command.CommandSender;

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

        localPlayer.toBukkit().teleportAsync(WorldProvider.KP_WORLD.getSpawnLocation()).whenComplete((result, throwable) -> {
            if(result) {
                localPlayer.msg(Messages.COMMAND_JOIN_SUCCESSFULLY);
                if(localPlayer.isSafe()) {
                    for (HotbarItemStack hotbarItem : Controllers.PLAYER.getHotbarItems()) {
                        localPlayer.toBukkit().getInventory().addItem(hotbarItem.getItemStack());
                    }
                }
                localPlayer.setInArena(false);

            } else {
                localPlayer.msg(Messages.COMMAND_JOIN_TELEPORT_ISSUE);
            }
        });

        if(!localPlayer.toBukkit().getInventory().isEmpty()) {
            localPlayer.msg(Messages.COMMAND_JOIN_UNSAFE);
            localPlayer.setSafeStatus(false);
        }
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
        if(target == null) {
            if(localPlayer.toBukkit().hasPermission("lonkskit.kitpvp.stats")) {
                localPlayer.msg("&eYour statistics:");
                localPlayer.msg("&7Kills: &b{0}", localPlayer.getMetrics().getKillsCount());
                localPlayer.msg("&7Deaths: &b{0}", localPlayer.getMetrics().getDeathsCount());
                localPlayer.msg("&7K/D Ratio: &e{0}", localPlayer.getMetrics().getKDR());
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
                localPlayer.msg("&7Points: &e{0}", target.getPoints());
            } else {
                localPlayer.msg(Messages.COMMAND_ENGINE_PERMISSION_DENIED);
            }
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
                localPlayer.setSelectedKit(null);
                localPlayer.getInventory().clear();
                localPlayer.toBukkit().getActivePotionEffects().forEach(activePe -> localPlayer.toBukkit().removePotionEffect(activePe.getType()));

                localPlayer.msg("&b[LonksKit] &cYou have cleared your kit.");
            } else {
                localPlayer.msg(Messages.COMMAND_ENGINE_PERMISSION_DENIED);
            }
        } else {
            if(localPlayer.toBukkit().hasPermission("lonkskit.clearkit.other")) {
                target.setSelectedKit(null);
                target.getInventory().clear();
                target.toBukkit().getActivePotionEffects().forEach(activePe -> target.toBukkit().removePotionEffect(activePe.getType()));

                localPlayer.msg("&b[LonksKit] &cYou have cleared {0}'s kit.", target.getLastKnownName());
                target.msg("&b[LonksKit] &eYour kit has been cleared by an admin.");
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
                localPlayer.msg("&b[LonksKit] &cYou don't have access to this command.");
            }
        } else {
            if(localPlayer.toBukkit().hasPermission("lonkskit.clearcooldown.other")) {
                clearCooldown(target);

                localPlayer.msg("&b[LonksKit] &cYou have cleared {0}'s cooldowns.", target.getLastKnownName());
                target.msg("&b[LonksKit] &eYour cooldowns have been cleared by an admin.");
            } else {
                localPlayer.msg("&b[LonksKit] &cYou don't have access to this command.");
            }
        }
    }

    private void clearCooldown(LocalPlayer localPlayer) {
        LonksKitProvider.getAbilityFactory().getAbilities()
                .forEach(ability -> ability.getCooldown().setLastTested(localPlayer, (System.currentTimeMillis() - ability.getCooldown().getBase().getTimeout())));
    }
}
