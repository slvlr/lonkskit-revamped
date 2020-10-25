package me.aiglez.lonkskit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import me.aiglez.lonkskit.WorldProvider;
import me.aiglez.lonkskit.players.LocalPlayer;
import org.bukkit.entity.Player;

@CommandAlias("leave|hub|spawn")
public class LeaveCommand extends BaseCommand {

    @Default
    public void onDefault(Player player) {
        final LocalPlayer localPlayer = LocalPlayer.get(player.getPlayer());
        if(!localPlayer.isValid()) {
            localPlayer.msg("&b[LonksKit] &cYou are already in the main world.");
            return;
        }

        if(localPlayer.inArena()) {
            localPlayer.msg("&b[LonksKit] &cYou need to be in the spawn to use this command.");
            return;
        }

        localPlayer.toBukkit().teleportAsync(WorldProvider.MAIN_WORLD.getSpawnLocation()).whenComplete((result, throwable) -> {
            if(result) {
                localPlayer.msg("&b[LonksKit] &aSuccessfully teleported to the main world");

                localPlayer.setSafeStatus(true);
                localPlayer.setInArena(false);

            } else {
                localPlayer.msg("&b[LonksKit] &cAn error occurred while trying to teleport you to the main world. Try later.");
            }
        });
    }

}
