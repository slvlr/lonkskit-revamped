package me.aiglez.lonkskit.commands.admin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.aiglez.lonkskit.players.LocalPlayer;

public class TeleportCommand extends BaseCommand {

    // -------------------------------------------- //
    // KIT TELEPORT
    // -------------------------------------------- //
    @CommandAlias("kittp")
    @CommandPermission("lonkskit.admin.teleport")
    @CommandCompletion("@kitpvp_players")
    @Syntax("<target>") @Description("Teleports you to the target's location.")
    public void onKitTeleport(LocalPlayer localPlayer, @Flags("other") LocalPlayer target) {
        if(!localPlayer.isValid() || !target.isValid()) {
            localPlayer.msg("&b[LonksKit] &cYou must be in the same world as the target to execute this command.");
            return;
        }

        localPlayer.toBukkit().teleportAsync(target.getLocation()).whenComplete((result, throwable) -> {
            if(result) {
                localPlayer.msg("&b[LonksKit] &eYou have been teleported to {0}'s location", target.getLastKnownName());
            } else {
                localPlayer.msg("&b[LonksKit] &cAn error occurred while trying to you to {0}'s location.", target.getLastKnownName());
            }
        });
    }

    // -------------------------------------------- //
    // KIT TELEPORT HERE
    // -------------------------------------------- //
    @CommandAlias("kittphere")
    @CommandPermission("lonkskit.admin.teleporthere")
    @CommandCompletion("@kitpvp_players")
    @Syntax("<target>") @Description("Teleports the target to your location.")
    public void onKitTeleportHere(LocalPlayer localPlayer, @Flags("other") LocalPlayer target) {
        if(!localPlayer.isValid() || !target.isValid()) {
            localPlayer.msg("&b[LonksKit] &cYou must be in the same world as the target to execute this command.");
            return;
        }

        target.toBukkit().teleportAsync(localPlayer.getLocation()).whenComplete((result, throwable) -> {
            if(result) {
                localPlayer.msg("&b[LonksKit] &eYou teleported {0} to your location.", target.getLastKnownName());
            } else {
                localPlayer.msg("&b[LonksKit] &cAn error occurred while trying to teleport {0} to you.", target.getLastKnownName());
            }
        });
    }
}
