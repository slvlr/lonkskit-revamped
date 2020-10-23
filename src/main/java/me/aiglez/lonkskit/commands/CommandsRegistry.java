package me.aiglez.lonkskit.commands;

import co.aikar.commands.PaperCommandManager;
import me.aiglez.lonkskit.KitPlugin;
import me.aiglez.lonkskit.utils.Logger;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.IOException;
import java.util.Locale;

public class CommandsRegistry {

    private final PaperCommandManager acf;

    public CommandsRegistry(KitPlugin plugin) {
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
        registerCommands();
    }

    private void loadLocales() throws IOException, InvalidConfigurationException {
        acf.getLocales().loadYamlLanguageFile("messages.yml", Locale.ENGLISH);
        acf.getLocales().setDefaultLocale(Locale.ENGLISH);
    }

    private void registerContexts() {

    }

    private void registerCommands() {
        acf.registerCommand(new MainCommand());
    }
}
