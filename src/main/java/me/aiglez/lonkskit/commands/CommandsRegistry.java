package me.aiglez.lonkskit.commands;

import co.aikar.commands.ACFBukkitUtil;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.PaperCommandManager;
import com.google.common.base.Preconditions;
import me.aiglez.lonkskit.KitPlugin;
import me.aiglez.lonkskit.LonksKitProvider;
import me.aiglez.lonkskit.WorldProvider;
import me.aiglez.lonkskit.commands.admin.AdminStatsCommand;
import me.aiglez.lonkskit.commands.admin.TeleportCommand;
import me.aiglez.lonkskit.commands.points.PointsCommand;
import me.aiglez.lonkskit.messages.Messages;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.players.OfflineLocalPlayer;
import me.aiglez.lonkskit.utils.Logger;
import me.lucko.helper.utils.Players;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.stream.Collectors;

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
        acf.registerCommand(new AdminStatsCommand());
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
    }

    private void registerContexts() {
        acf.getCommandContexts().registerContext(OfflineLocalPlayer.class, c -> {
            final String name = c.popFirstArg();
            final boolean optional = c.isOptional();

            final Optional<OfflineLocalPlayer> offlineLocalPlayer = LonksKitProvider.getPlayerFactory().getOfflineLocalPlayer(name);
            if(offlineLocalPlayer.isPresent()) {
                Logger.debug("[OfflineLocalPlayer Context] Found for {0}", name);
                return offlineLocalPlayer.get();
            } else {
                Logger.debug("[OfflineLocalPlayer Context] Couldn't found for {0}", name);
                if(optional) {
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
                        Logger.debug("[{0}] Username is not set and it is optional", c.getCmd().getCommand());
                        return null;
                    } else {
                        Logger.debug("[{0}] Username is not set", c.getCmd().getCommand());
                        throw new InvalidCommandArgument();
                    }
                }

                final Player player = ACFBukkitUtil.findPlayerSmart(c.getIssuer(), name);
                if (player == null) {
                    if (optional) {
                        Logger.debug("[{0}] Player with username: {1} was not found, but it's optional ", c.getCmd().getCommand(), name);
                        return null;
                    }
                    Logger.debug("[{0}] Player with username: {1} was not found", c.getCmd().getCommand(), name);
                    throw new InvalidCommandArgument(false);
                }

                return LocalPlayer.get(player);
            } else {
                final Player player = isPlayer ? (Player) sender : null;
                if(player == null && !optional) {
                    throw new InvalidCommandArgument("§cThis command is player only", false);
                }
                Logger.debug("[{0}] LocalPlayer instance of {1} was found", c.getCmd().getCommand(), player.getName());
                return LocalPlayer.get(player);
            }
        });
    }

    private void registerConditions() {
        this.acf.getCommandConditions().addCondition(LocalPlayer.class, "valid_world", (context, executionContext, localPlayer) -> {
            if(!localPlayer.isValid()) {
                throw new ConditionFailedException();
            }
        });
    }
}
