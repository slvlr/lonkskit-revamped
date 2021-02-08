package me.aiglez.lonkskit.commands.admin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.aiglez.lonkskit.kits.Kit;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.players.LocalRent;
import me.aiglez.lonkskit.players.OfflineLocalPlayer;
import me.aiglez.lonkskit.players.impl.MemoryLocalRent;

@CommandAlias("%main_command")
public class RentingCommand extends BaseCommand {
    @Subcommand("admin renting")
    @CommandCompletion("show|remove|add @kitpvp_offline_players @kit")
    @CommandPermission("lonkskit.admin.renting")
    @Syntax("<show|remove|add> <target> [kit]")
    public void onAdminRenting(LocalPlayer localPlayer, @Values("show|remove|add") String operation, OfflineLocalPlayer target, @Optional Kit kit){
        if (operation.equalsIgnoreCase("show")){
            if (target != null){
                if (kit != null){
                    localPlayer.msg("renting of " + target.getLastKnownName() + " :");
                    target.getRent(kit).ifPresent(localRent -> {
                        localPlayer.msg("&4uses till now : " + localRent.getUses());
                        localPlayer.msg("&4left uses : " + localRent.getLeftUses());
                    });
                    if (!target.getRent(kit).isPresent()){
                        localPlayer.msg("the player don't rent the kit : " + kit.getDisplayName());
                    }
                }else localPlayer.msg("&5the kit is null");
            }else localPlayer.msg("&5Player not found 404");
        }
        if (operation.equalsIgnoreCase("remove")){
            if (target != null){
                if (kit != null){
                    target.removeRent(LocalRent.of(target,kit));
                    localPlayer.msg("kit Rent is deleted successfully");
                }else localPlayer.msg("kit is null");
            }else localPlayer.msg("Target == null");
        }
        if (operation.equalsIgnoreCase("add")){
            if (target != null){
                if (kit != null){
                    target.addRent(new MemoryLocalRent(target,kit));
                    localPlayer.msg("kit Rent added successfully");
                }else localPlayer.msg("kit is null");
            }else localPlayer.msg("Target == null");
        }
    }

}
