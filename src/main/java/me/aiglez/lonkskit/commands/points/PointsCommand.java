package me.aiglez.lonkskit.commands.points;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.aiglez.lonkskit.messages.Messages;
import me.aiglez.lonkskit.players.LocalPlayer;

@CommandAlias("point|points")
public class PointsCommand extends BaseCommand {

    @Default
    @Syntax("[target]") @Description("Show your or target's points.")
    public void onPoints(@Conditions("valid_world") LocalPlayer localPlayer, @Conditions("valid_world") @Flags("other") @Optional LocalPlayer target) {
        if(target == null) {
            localPlayer.msg(Messages.COMMAND_POINTS_SHOW, localPlayer.getPoints());
        } else {
            localPlayer.msg(Messages.COMMAND_POINTS_SHOW_OTHER, target.getLastKnownName(), target.getPoints());
        }
    }

    @Subcommand("pay")
    @CommandCompletion("@kitpvp_players @range:1-10000")
    @Syntax("<player> <amount>") @Description("Send the specified amount of points to the target.")
    public void onPointsPay(@Conditions("valid_world") LocalPlayer localPlayer, @Conditions("valid_world") @Flags("other") LocalPlayer target, int amount) {
        if(localPlayer.getPoints() < amount) {
            localPlayer.msg(Messages.COMMAND_POINTS_PAY_NOT_ENOUGH);
            return;
        }

        final boolean result = localPlayer.decrementPoints(amount);
        if(!result) {
            localPlayer.msg(Messages.COMMAND_POINTS_PAY_FAILED);
            return;
        }
        target.incrementPoints(amount);

        localPlayer.msg(Messages.COMMAND_POINTS_PAY_SENT, amount, target.getLastKnownName());
        target.msg(Messages.COMMAND_POINTS_PAY_RECEIVED, amount, localPlayer.getLastKnownName());
    }

}
