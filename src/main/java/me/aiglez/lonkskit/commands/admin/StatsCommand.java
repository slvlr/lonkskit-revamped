package me.aiglez.lonkskit.commands.admin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.aiglez.lonkskit.messages.Messages;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.players.OfflineLocalPlayer;

@CommandAlias("%main_command")
public class StatsCommand extends BaseCommand {

    // -------------------------------------------- //
    // POINTS
    // -------------------------------------------- //
    @Subcommand("admin points")
    @CommandCompletion("set|give|take|reset @kitpvp_offline_players")
    @CommandPermission("lonkskit.admin.stats")
    @Syntax("<set|give|take|reset> <target> [amount]")
    public void onAdminPoints(LocalPlayer localPlayer, @Values("set|give|take|reset") String operation, OfflineLocalPlayer target, @Optional Integer amount) {
        switch (operation.toLowerCase()) {
            case "set":
                if(amount != null && amount > 0) {
                    target.setPoints(amount);

                    localPlayer.msg(Messages.COMMAND_ADMIN_STATS_POINTS_SET, target.getLastKnownName(), amount);
                } else {
                    localPlayer.msg(Messages.COMMAND_ENGINE_MUST_BE_INT);
                }
                break;

            case "give":
                if(amount != null && amount > 0) {
                    target.incrementPoints(amount);
                    localPlayer.msg(Messages.COMMAND_ADMIN_STATS_POINTS_GIVE, amount, target.getLastKnownName());
                } else {
                    localPlayer.msg(Messages.COMMAND_ENGINE_MUST_BE_INT);
                }
                break;
            case "take":
                if(amount != null && amount > 0) {
                    final int remove = (target.getPoints() - amount < 0 ? 0 : target.getPoints());
                    localPlayer.decrementPoints(remove);
                    localPlayer.msg(Messages.COMMAND_ADMIN_STATS_POINTS_TAKE, remove, target.getLastKnownName());
                } else {
                    localPlayer.msg(Messages.COMMAND_ENGINE_MUST_BE_INT);
                }
                break;

            case "reset":
                target.setPoints(0);
                localPlayer.msg(Messages.COMMAND_ADMIN_STATS_POINTS_RESET, target.getLastKnownName());
                break;
        }
    }

    // -------------------------------------------- //
    // KILLS
    // -------------------------------------------- //
    @Subcommand("admin kills")
    @CommandCompletion("set|give|take|reset @kitpvp_offline_players")
    @CommandPermission("lonkskit.admin.stats")
    @Syntax("<set|give|take|reset> <target> [amount]")
    public void onAdminKills(LocalPlayer localPlayer, @Values("set|give|take|reset") String operation, OfflineLocalPlayer target, @Optional Integer amount) {
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
                    localPlayer.msg(Messages.COMMAND_ADMIN_STATS_KILLS_GIVE, amount, target.getLastKnownName());
                } else {
                    localPlayer.msg(Messages.COMMAND_ENGINE_MUST_BE_INT);
                }
                break;
            case "take":
                if(amount != null && amount > 0) {
                    final int old = target.getMetrics().getKillsCount();
                    final int remove = (target.getMetrics().getKillsCount() - amount < 0 ? 0 : target.getMetrics().getKillsCount());
                    target.getMetrics().updateAll(old - remove, target.getMetrics().getDeathsCount());
                    localPlayer.msg(Messages.COMMAND_ADMIN_STATS_KILLS_TAKE, remove, target.getLastKnownName());
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

    // -------------------------------------------- //
    // DEATHS
    // -------------------------------------------- //
    @Subcommand("admin deaths")
    @CommandCompletion("set|give|take|reset @kitpvp_offline_players")
    @CommandPermission("lonkskit.admin.stats")
    @Syntax("<set|give|take|reset> <target> [amount]")
    public void onAdminDeaths (LocalPlayer localPlayer, @Values("set|give|take|reset") String operation, OfflineLocalPlayer target, @Optional Integer amount) {
        switch (operation.toLowerCase()) {
            case "set":
                if(amount != null && amount > 0) {
                    target.getMetrics().updateAll(target.getMetrics().getKillsCount(), amount);
                    localPlayer.msg(Messages.COMMAND_ADMIN_STATS_DEATHS_SET, target.getLastKnownName(), amount);
                } else {
                    localPlayer.msg(Messages.COMMAND_ENGINE_MUST_BE_INT);
                }
                break;

            case "give":
                if(amount != null && amount > 0) {
                    final int old = target.getMetrics().getDeathsCount();
                    target.getMetrics().updateAll(target.getMetrics().getKillsCount(), old + amount);
                    localPlayer.msg(Messages.COMMAND_ADMIN_STATS_DEATHS_GIVE, amount, target.getLastKnownName());
                } else {
                    localPlayer.msg(Messages.COMMAND_ENGINE_MUST_BE_INT);
                }
                break;
            case "take":
                if(amount != null && amount > 0) {
                    final int old = target.getMetrics().getDeathsCount();
                    final int remove = (target.getMetrics().getDeathsCount() - amount < 0 ? 0 : target.getMetrics().getDeathsCount());
                    target.getMetrics().updateAll(target.getMetrics().getKillsCount(), old - remove);
                    localPlayer.msg(Messages.COMMAND_ADMIN_STATS_DEATHS_TAKE, remove, target.getLastKnownName());
                } else {
                    localPlayer.msg(Messages.COMMAND_ENGINE_MUST_BE_INT);
                }
                break;

            case "reset":
                target.getMetrics().updateAll(target.getMetrics().getKillsCount(), 0);
                localPlayer.msg(Messages.COMMAND_ADMIN_STATS_DEATHS_RESET, target.getLastKnownName());

                break;
        }
    }
}
