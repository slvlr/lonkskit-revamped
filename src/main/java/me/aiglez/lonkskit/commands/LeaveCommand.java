package me.aiglez.lonkskit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Syntax;
import me.aiglez.lonkskit.WorldProvider;
import me.aiglez.lonkskit.messages.Messages;
import me.aiglez.lonkskit.players.LocalPlayer;

@CommandAlias("leave|hub|spawn")
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

        localPlayer.toBukkit().teleportAsync(WorldProvider.MAIN_WORLD.getSpawnLocation()).whenComplete((result, throwable) -> {
            if(result) {
                localPlayer.msg(Messages.COMMAND_LEAVE_SUCCESSFULLY);

                localPlayer.setSafeStatus(true);
                localPlayer.setInArena(false);

            } else {
                localPlayer.msg(Messages.COMMAND_LEAVE_TELEPORT_ISSUE);
            }
        });
    }

}
