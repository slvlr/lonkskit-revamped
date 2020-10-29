package me.aiglez.lonkskit.commands.admin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.aiglez.lonkskit.messages.Messages;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.players.OfflineLocalPlayer;

@CommandAlias("%main_command admin")
@CommandPermission("lonkskit.admin.stats")
public class AdminStatsCommand extends BaseCommand {

    // -------------------------------------------- //
    // KILLS
    // -------------------------------------------- //
    @Subcommand("kills")
    @Conditions("valid_world")
    @CommandCompletion("set|give|take|reset @kitpvp_offline_players")
    @Syntax("<set|give|take|reset> <target> [amount]")
    public void onAdminKills(LocalPlayer localPlayer, @Values("set give take reset") String operation, OfflineLocalPlayer target, @Optional Integer amount) {
        switch (operation.toLowerCase()) {
            case "set":
                if(amount != null && amount > 0) {
                    target.getMetrics().updateAll(amount, target.getMetrics().getDeathsCount());
                    localPlayer.msg(Messages.COMMAND_ADMIN_STATS_KILLS_SET, target.getLastKnownName(), amount);
                } else {
                    localPlayer.msg(Messages.COMMAND_ENGINE_MUST_BE_INT);
                }
                break;

            case "give":
                if(amount != null && amount > 0) {
                    final int old = target.getMetrics().getKillsCount();
                    target.getMetrics().updateAll(old + amount, target.getMetrics().getDeathsCount());
                    localPlayer.msg(Messages.COMMAND_ADMIN_STATS_KILLS_SET, amount, target.getLastKnownName());
                } else {
                    localPlayer.msg(Messages.COMMAND_ENGINE_MUST_BE_INT);
                }
                break;
            case "take":
                if(amount != null && amount > 0) {
                    final int remove = (target.getMetrics().getKillsCount() - amount < 0 ? 0 : target.getMetrics().getKillsCount());
                    target.getMetrics().updateAll(remove, target.getMetrics().getDeathsCount());
                    localPlayer.msg(Messages.COMMAND_ADMIN_STATS_KILLS_TAKE, amount, target.getLastKnownName());
                } else {
                    localPlayer.msg(Messages.COMMAND_ENGINE_MUST_BE_INT);
                }
                break;

            case "reset":
                target.getMetrics().updateAll(0, target.getMetrics().getDeathsCount());
                localPlayer.msg(Messages.COMMAND_ADMIN_STATS_KILLS_RESET, target.getLastKnownName());

                break;
        }
    }


}
