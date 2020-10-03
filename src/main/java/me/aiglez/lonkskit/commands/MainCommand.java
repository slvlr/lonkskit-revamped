package me.aiglez.lonkskit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import org.bukkit.entity.Player;

@CommandAlias("kitpvp")
public class MainCommand extends BaseCommand {


    // -------------------------------------------- //
    // DEFAULT
    // -------------------------------------------- //
    @Default @CommandPermission("lonkskit.cmd.kitpvp")
    public void onDefault(Player player) {




    }


}
