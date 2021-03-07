package me.aiglez.lonkskit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Syntax;
import me.aiglez.lonkskit.WorldProvider;
import me.aiglez.lonkskit.messages.Messages;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.Logger;

/**
 * Handles the leave command
 */
@CommandAlias("leave|hub|lobby")
public class LeaveCommand extends BaseCommand {

    @Default
    @Syntax("") @Description("Go back to Towny world.")
    public void onDefault(LocalPlayer localPlayer) {
        if(!localPlayer.isValid()) {
            localPlayer.msg(Messages.COMMAND_LEAVE_ERROR);
            return;
        }

        if(localPlayer.inArena()) {
            localPlayer.msg(Messages.COMMAND_LEAVE_MUSTBEATSPAWN);
            return;
        }

        if(!localPlayer.isSafe()) {
            Logger.warn("The player {0} tried to go to the hub but he is not safe", localPlayer.getLastKnownName());
            return;
        }
        if (MainCommand.check(localPlayer)){
            localPlayer.msg("&4You can't teleport to any other worlds cause you have a 'Throwable' item");
            return;
        }
        if (localPlayer.hasSelectedKit()){
            localPlayer.msg("First clear your kit ");
            return;
        }
        localPlayer.toBukkit().teleportAsync(WorldProvider.MAIN_WORLD.getSpawnLocation()).whenComplete((result, throwable) -> {
            if(result) {
                localPlayer.msg(Messages.COMMAND_LEAVE_SUCCESSFULLY);
                localPlayer.setInKP(false);
                localPlayer.setSafeStatus(true);
                localPlayer.setInArena(false);
                localPlayer.getInventory().clear();
                localPlayer.setSelectedKit(null);

            } else {
                localPlayer.msg(Messages.COMMAND_LEAVE_TELEPORT_ISSUE);
            }
        });
    }

}
