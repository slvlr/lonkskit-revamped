package me.aiglez.lonkskit.commands;

import co.aikar.commands.ACFBukkitUtil;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.PaperCommandManager;
import com.google.common.base.Preconditions;
import me.aiglez.lonkskit.KitPlugin;
import me.aiglez.lonkskit.LonksKitProvider;
import me.aiglez.lonkskit.WorldProvider;
import me.aiglez.lonkskit.commands.admin.RentingCommand;
import me.aiglez.lonkskit.commands.admin.StatsCommand;
import me.aiglez.lonkskit.commands.admin.TeleportCommand;
import me.aiglez.lonkskit.commands.points.PointsCommand;
import me.aiglez.lonkskit.kits.Kit;
import me.aiglez.lonkskit.messages.Messages;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.players.OfflineLocalPlayer;
import me.aiglez.lonkskit.struct.leaderboards.StatsLeaderboard;
import me.aiglez.lonkskit.utils.Logger;
import me.lucko.helper.Commands;
import me.lucko.helper.hologram.Hologram;
import me.lucko.helper.hologram.HologramFactory;
import me.lucko.helper.utils.Players;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CommandsRegistry {

    private final PaperCommandManager acf;
    public CommandsRegistry(KitPlugin plugin) {
        Preconditions.checkNotNull(plugin, "plugin instance may not be null");
        this.acf = new PaperCommandManager(plugin);
        this.acf.enableUnstableAPI("brigadier");
        this.acf.enableUnstableAPI("help");
        this.acf.setDefaultExceptionHandler((command, registeredCommand, sender, args, t) -> {
            Logger.warn("An error occurred while executing command: {0}", command.getName());
            if(t != null) t.printStackTrace();
            return false;
        });

        loadLocales();
        registerReplacements();
        registerContexts();
        registerCompletitions();
        registerConditions();
        registerCommands();
    }

    private void loadLocales() {
        Messages.setACFMessages(this.acf.getLocales());
    }

    private void registerCommands() {
        acf.registerCommand(new MainCommand());
        acf.registerCommand(new LeaveCommand());
        acf.registerCommand(new TeleportCommand());
        acf.registerCommand(new PointsCommand());
        acf.registerCommand(new StatsCommand());
        acf.registerCommand(new RentingCommand());
    }

    private void registerReplacements() {
        acf.getCommandReplacements().addReplacements(
                "main_command", "kitpvp"
        );
    }

    private void registerCompletitions() {
        acf.getCommandCompletions().registerAsyncCompletion("kitpvp_players", c -> Players.stream()
                .filter(player -> player.getWorld().getUID().equals(WorldProvider.KP_WORLD.getUID()))
                .map(HumanEntity::getName)
                .collect(Collectors.toList()));

        acf.getCommandCompletions().registerAsyncCompletion("kitpvp_offline_players", c -> LonksKitProvider.getPlayerFactory().getCachedOfflinePlayers()
                .stream()
                .filter(offlineLocalPlayer -> offlineLocalPlayer.getLastKnownName() != null)
                .map(OfflineLocalPlayer::getLastKnownName)
                .collect(Collectors.toList()));
        acf.getCommandCompletions().registerAsyncCompletion("kit",c -> LonksKitProvider.getKitFactory().getRegisteredKits()
                .stream()
                .map(Kit::getBackendName)
                .collect(Collectors.toList()));
        acf.getCommandCompletions().registerAsyncCompletion("amount",c -> IntStream.rangeClosed(0,100).boxed().map(String::valueOf).collect(Collectors.toList()));

    }

    private void registerContexts() {
        acf.getCommandContexts().registerContext(OfflineLocalPlayer.class, c -> {
            final String name = c.popFirstArg();
            final boolean optional = c.isOptional();

            final Optional<OfflineLocalPlayer> offlineLocalPlayer = LonksKitProvider.getPlayerFactory().getOfflineLocalPlayer(name);
            if(offlineLocalPlayer.isPresent()) {
                return offlineLocalPlayer.get();
            } else {
                if(optional) {
                    return null;
                }
                throw new InvalidCommandArgument("§b[LonksKit] §cCouldn't find a player matching that name.", false);
            }
        });
        acf.getCommandContexts().registerContext(Kit.class,c ->{
            final String name = c.popFirstArg();
            final boolean optional = c.isOptional();
            final Optional<Kit> kit = LonksKitProvider.getKitFactory().getKit(name);
            if (kit.isPresent()){
                return kit.get();
            }else {
                if (optional){
                    return null;
                }
                throw new InvalidCommandArgument("§b[LonksKit] §cCouldn't find a player matching that name.", false);
            }
        });

        acf.getCommandContexts().registerIssuerAwareContext(LocalPlayer.class, c -> {
            final boolean optional = c.isOptional();
            final CommandSender sender = c.getSender();
            final boolean isPlayer = sender instanceof Player;

            if(c.hasFlag("other")) {
                final String name = c.popFirstArg();
                if(name == null) {
                    if(optional) {
                        return null;
                    } else {
                        throw new InvalidCommandArgument();
                    }
                }

                final Player player = ACFBukkitUtil.findPlayerSmart(c.getIssuer(), name);
                if (player == null) {
                    if (optional) {
                        return null;
                    }
                    throw new InvalidCommandArgument(false);
                }

                return LocalPlayer.get(player);
            } else {
                final Player player = isPlayer ? (Player) sender : null;
                if(player == null && !optional) {
                    throw new InvalidCommandArgument("§cThis command is player only", false);
                }
                return LocalPlayer.get(player);
            }
        });
    }

    private void registerConditions() {
        this.acf.getCommandConditions().addCondition(LocalPlayer.class, "valid_world", (context, executionContext, localPlayer) -> {
            if (localPlayer == null) {
                return;
            }
            if(!localPlayer.isValid()) {
                throw new ConditionFailedException();
            }
        });
    }
}
