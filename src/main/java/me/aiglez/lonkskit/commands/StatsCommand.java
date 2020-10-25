package me.aiglez.lonkskit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.entity.Player;

@CommandAlias("%main_command")
public class StatsCommand extends BaseCommand {

    @Subcommand("stats")
    public void onStats(Player player, @Optional Player target) {

    }

}
