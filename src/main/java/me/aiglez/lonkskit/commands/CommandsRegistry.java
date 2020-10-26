package me.aiglez.lonkskit.commands;

import co.aikar.commands.ACFBukkitUtil;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.PaperCommandManager;
import com.google.common.base.Preconditions;
import me.aiglez.lonkskit.KitPlugin;
import me.aiglez.lonkskit.LonksKitProvider;
import me.aiglez.lonkskit.WorldProvider;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.players.OfflineLocalPlayer;
import me.aiglez.lonkskit.utils.Logger;
import me.lucko.helper.utils.Players;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public class CommandsRegistry {

    private final PaperCommandManager acf;

    public CommandsRegistry(KitPlugin plugin) {
        Preconditions.checkNotNull(plugin, "plugin instance may not be null");
        this.acf = new PaperCommandManager(plugin);
        this.acf.enableUnstableAPI("brigadier");
        this.acf.setDefaultExceptionHandler((command, registeredCommand, sender, args, t) -> {
            Logger.warn("An error occurred while executing command: {0}", command.getName());
            if(t != null) t.printStackTrace();
            return false;
        });

        try {
            loadLocales();
        } catch (IOException | InvalidConfigurationException e) {
            Logger.severe("Couldn't load messages.yml.");
            e.printStackTrace();
        }

        registerReplacements();
        registerContexts();
        registerCompletitions();
        registerCommands();
    }

    private void loadLocales() throws IOException, InvalidConfigurationException {
        acf.getLocales().loadYamlLanguageFile("messages.yml", Locale.ENGLISH);
        acf.getLocales().setDefaultLocale(Locale.ENGLISH);
    }

    private void registerCommands() {
        acf.registerCommand(new MainCommand());
        acf.registerCommand(new LeaveCommand());
        acf.registerCommand(new StatsCommand());
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

        acf.getCommandContexts().registerContext(LocalPlayer.class, c -> {
            final boolean optional = c.isOptional();
            final Player player = ACFBukkitUtil.findPlayerSmart(c.getIssuer(), c.popFirstArg());
            if (player == null) {
                if (optional) {
                    return null;
                }
                throw new InvalidCommandArgument(false);
            }

            return LocalPlayer.get(player);
        });
    }
}
