package me.aiglez.lonkskit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import me.aiglez.lonkskit.LonksKitProvider;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.players.OfflineLocalPlayer;
import org.bukkit.entity.Player;

import java.util.Optional;

@CommandAlias("kitpvp")
public class MainCommand extends BaseCommand {

    // -------------------------------------------- //
    // DEFAULT
    // -------------------------------------------- //
    @Default @Subcommand("join")
    @CommandPermission("lonkskit.kitpvp")
    public void onDefault(Player player) {
        final LocalPlayer online = LocalPlayer.get(player.getPlayer());
        final OfflineLocalPlayer offline = LonksKitProvider.getPlayerFactory().getOfflineLocalPlayer(player.getUniqueId());

        if(online == null) {
            player.sendMessage("§cThe local player instance (online) was not found");
            return;
        }

        online.msg("&aIs Online: {0} | &eName: {1} | &6is Valid: {2}", online.isOnline(), online.getLastKnownName(), online.isValid());

        if(offline == null) {
            online.msg("&cThe offline instance was not found");
            return;
        }

        online.msg("&aIs Online (offline instance): {0}", offline.isOnline());
        online.msg("(offline) &aIs Online: {0} | &eName: {1}", offline.isOnline(), offline.getLastKnownName());


        final Optional<OfflineLocalPlayer> offlineByName = LonksKitProvider.getPlayerFactory().getOfflineLocalPlayer(player.getName());
        if(!offlineByName.isPresent()) {
            online.msg("&cThe offline instance by name was not found!");
            return;
        }

        online.msg("&4(offline by name) &aIs Online: {0} | &eName: {1}", offline.isOnline(), offline.getLastKnownName());
        /*
        if(localPlayer.isValid()) {
            localPlayer.msg("&b[LonksKit] &cYou are already in the Kit PvP world.");
            return;
        }

        localPlayer.toBukkit().teleportAsync(WorldProvider.KP_WORLD.getSpawnLocation()).whenComplete((result, throwable) -> {
            if(result) {
                localPlayer.msg("&b[LonksKit] &aWelcome in the Kit PvP world !");
                if(localPlayer.isSafe()) {
                    for (HotbarItemStack hotbarItem : Controllers.PLAYER.getHotbarItems()) {
                        localPlayer.toBukkit().getInventory().addItem(hotbarItem.getItemStack());
                    }
                }
                localPlayer.setInArena(false);

            } else {
                localPlayer.msg("&b[LonksKit] &cAn error occurred while trying to teleport you to the Kit PvP world. Try later.");
            }
        });

        if(!localPlayer.toBukkit().getInventory().isEmpty()) {
            localPlayer.msg("&b[LonksKit] &cYou have items in your inventory, you won't be able to play, you need to put those items in your enderchest.");
            localPlayer.setSafeStatus(false);
        }

         */
    }
}
