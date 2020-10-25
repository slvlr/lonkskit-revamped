package me.aiglez.lonkskit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import me.aiglez.lonkskit.WorldProvider;
import me.aiglez.lonkskit.controllers.Controllers;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.struct.HotbarItemStack;
import org.bukkit.entity.Player;

@CommandAlias("kitpvp")
public class MainCommand extends BaseCommand {

    // -------------------------------------------- //
    // DEFAULT
    // -------------------------------------------- //
    @Default @Subcommand("join")
    @CommandPermission("lonkskit.kitpvp")
    public void onDefault(Player player) {
        final LocalPlayer localPlayer = LocalPlayer.get(player.getPlayer());
        if(localPlayer.isValid()) {
            localPlayer.msg("&b[LonksKit] &cYou are already in the Kit PvP world.");
            return;
        }

        localPlayer.toBukkit().teleportAsync(WorldProvider.KP_WORLD.getSpawnLocation()).whenComplete((result, throwable) -> {
            if(result) {
                localPlayer.msg("&b[LonksKit] &aWelcome in the Kit PvP world !");
                if(localPlayer.isSafe()) {
                    for (HotbarItemStack hotbarItem : Controllers.PLAYER.getHotbarItems()) {
                        localPlayer.toBukkit().getInventory().addItem(hotbarItem.getItemStack());
                    }
                }
                localPlayer.setInArena(false);

            } else {
                localPlayer.msg("&b[LonksKit] &cAn error occurred while trying to teleport you to the Kit PvP world. Try later.");
            }
        });

        if(!localPlayer.toBukkit().getInventory().isEmpty()) {
            localPlayer.msg("&b[LonksKit] &cYou have items in your inventory, you won't be able to play, you need to put those items in your enderchest.");
            localPlayer.setSafeStatus(false);
        }
    }
}
