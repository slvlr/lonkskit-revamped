package me.aiglez.lonkskit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import me.aiglez.lonkskit.abilities.Ability;
import me.aiglez.lonkskit.players.LocalPlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

@CommandAlias("kitpvp")
public class MainCommand extends BaseCommand {


    // -------------------------------------------- //
    // DEFAULT
    // -------------------------------------------- //
    @Default @CommandPermission("lonkskit.cmd.kitpvp")
    public void onDefault(Player player) {
        final LocalPlayer localPlayer = LocalPlayer.get(player);

        localPlayer.msg("&eLonksKit - DEBUG");
        localPlayer.msg("&bYour kit: &7{0}", localPlayer.hasSelectedKit() ? localPlayer.getNullableSelectedKit().getBackendName() : "&cNone");

        if(localPlayer.hasSelectedKit()) {
            List<String> abilities = localPlayer.getNullableSelectedKit().getAbilities().stream().map(Ability::getName).collect(Collectors.toList());
            localPlayer.msg("&bAbilities (you have access to): &7{0}", abilities);
        }
    }
}
