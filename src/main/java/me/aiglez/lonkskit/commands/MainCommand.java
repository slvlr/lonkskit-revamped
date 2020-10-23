package me.aiglez.lonkskit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import me.aiglez.lonkskit.WorldProvider;
import me.aiglez.lonkskit.controllers.Controllers;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.struct.LoginItemStack;

@CommandAlias("kitpvp")
public class MainCommand extends BaseCommand {

    // -------------------------------------------- //
    // DEFAULT
    // -------------------------------------------- //
    @Default @CommandPermission("lonkskit.kitpvp")
    public void onDefault(LocalPlayer localPlayer) {
        if(WorldProvider.inKPWorld(localPlayer)) {
            localPlayer.msg("&b[LonksKit] &cYou are already in the Kit PvP world.");
            return;
        }

        if(!localPlayer.toBukkit().getInventory().isEmpty()) {
            localPlayer.msg("&b[LonksKit] &cYou have items in your inventory, you won't be able to player, you need to put those items in your enderchest.");
            localPlayer.setSafeStatus(false);
        }

        localPlayer.toBukkit().teleportAsync(WorldProvider.KP_WORLD.getSpawnLocation()).whenComplete((result, throwable) -> {
            if(result) {
                localPlayer.msg("&b[LonksKit] &aWelcome in the Kit PvP world !");
                if(localPlayer.isSafe()) {
                    for (LoginItemStack loginItemStack : Controllers.PLAYER.getLoginItems()) {
                        localPlayer.toBukkit().getInventory().addItem(loginItemStack.getItemStack());
                    }
                }
                localPlayer.setAtArena(false);

            } else {
                localPlayer.msg("&b[LonksKit] &cAn error occurred while trying to teleport you to the Kit PvP world. Try later.");
            }
        });
    }
}
