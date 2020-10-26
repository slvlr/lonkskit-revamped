package me.aiglez.lonkskit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.players.OfflineLocalPlayer;

@CommandAlias("%main_command")
public class StatsCommand extends BaseCommand {

    @Subcommand("stats")
    @Syntax("[target]")
    @CommandCompletion("@kitpvp_players")
    public void onStats(LocalPlayer localPlayer, @Optional LocalPlayer target) {
        localPlayer.msg("is target found: {0}", (target != null));
    }

    @Subcommand("statsoff")
    @Syntax("<target>")
    @CommandCompletion("@kitpvp_offline_players")
    @CommandPermission("lonkskit.stats")
    public void onOfflineStats(LocalPlayer localPlayer, OfflineLocalPlayer target) {
        localPlayer.msg("is target found: {0}", (target != null));
        if(target != null) {
            localPlayer.msg("{0}'s points : {1}", target.getLastKnownName(), target.getPoints());
        }
    }

}
