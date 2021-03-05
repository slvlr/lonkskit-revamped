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
    @CommandCompletion("show|remove|add @kitpvp_offline_players @kit @range:1-100")
    @CommandPermission("lonkskit.admin.renting")
    @Syntax("<show|remove|add> <target> [kit] [amount]")
    public void onAdminRenting(LocalPlayer localPlayer, @Values("show|remove|add") String operation, OfflineLocalPlayer target, @Optional Kit kit,@Optional Integer amount){
        if (operation.equalsIgnoreCase("show")){
            if (target != null){
                if (kit != null){
                    target.getRent(kit).ifPresent(localRent -> {
                        localPlayer.msg("&7{0} is currently renting &b{1}:",target.getLastKnownName(),kit.getDisplayName());//
                        localPlayer.msg("&cTimes used: " + localRent.getUses());
                        localPlayer.msg("&aUses left: " + localRent.getLeftUses());
                    });
                    if (!target.getRent(kit).isPresent()){
                        localPlayer.msg("&7{0} is not renting &b{1}.",target.getLastKnownName(),kit.getDisplayName());
                    }
                }else {
                    localPlayer.msg("&7{0} is renting the following kits: ",target.getLastKnownName());
                    for (LocalRent rent:
                         target.getRents()) {
                        localPlayer.msg("&b" + rent.getRented().getDisplayName() + " --> " + rent.getLeftUses() + " Uses left");
                    }
                }
            }else localPlayer.msg("&5Player not found 404");
        }
        if (operation.equalsIgnoreCase("remove")){
            if (target != null){
                if (kit != null){
                    target.removeRent(LocalRent.of(target,kit));
                    localPlayer.msg("kit Rent is deleted successfully");
                }else localPlayer.msg("kit is null");
            }else localPlayer.msg("Target is null");
        }
        if (operation.equalsIgnoreCase("add")){
            if (target != null){
                if (kit != null){
                    if (kit.isRentable()) {
                        target.addRent(new MemoryLocalRent(target, kit, kit.getUsesPerRent() - amount));
                        localPlayer.msg("kit Rent added successfully");
                    }else localPlayer.msg("&4The kit is not rentable check the config file");
                }else localPlayer.msg("kit is null");
            }else localPlayer.msg("Target == null");
        }
    }

}
