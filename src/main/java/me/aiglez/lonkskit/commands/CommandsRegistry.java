package me.aiglez.lonkskit.commands;

import co.aikar.commands.PaperCommandManager;
import me.aiglez.lonkskit.KitPlugin;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.IOException;
import java.util.Locale;

public class CommandsRegistry {

    private final PaperCommandManager acf;

    public CommandsRegistry(KitPlugin plugin) {
        this.acf = new PaperCommandManager(plugin);
        this.acf.enableUnstableAPI("brigadier");
        try {
            loadLocales();
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        registerCommands();
    }

    private void loadLocales() throws IOException, InvalidConfigurationException {
        acf.getLocales().loadYamlLanguageFile("messages.yml", Locale.ENGLISH);
        acf.getLocales().setDefaultLocale(Locale.ENGLISH);
    }

    private void registerCompletions() {
    }

    private void registerCommands() {
        acf.registerCommand(new MainCommand());
    }
}
