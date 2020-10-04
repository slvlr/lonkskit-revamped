package me.aiglez.lonkskit;

import me.aiglez.lonkskit.abilities.factory.AbilityFactory;
import me.aiglez.lonkskit.commands.CommandsRegistry;
import me.aiglez.lonkskit.kits.KitFactory;
import me.aiglez.lonkskit.listeners.AbilityListeners;
import me.aiglez.lonkskit.listeners.PlayerListeners;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.players.LocalPlayerFactory;
import me.aiglez.lonkskit.utils.Logger;
import me.lucko.helper.config.ConfigFactory;
import me.lucko.helper.config.ConfigurationNode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;

public final class KitPlugin extends JavaPlugin implements Listener {

    private static KitPlugin singleton;
    private KitFactory kitFactory;
    private LocalPlayerFactory localPlayerFactory;
    private AbilityFactory abilityFactory;
    private ConfigurationNode conf;


    @Override
    public void onEnable() {
        singleton = this;
        Logger.fine("Loading configuration files...");
        this.conf = ConfigFactory.yaml().load(new File(getDataFolder(), "config.yml"));

        Logger.fine("Loading abilities...");
        this.abilityFactory = AbilityFactory.make();
        this.abilityFactory.registerAbilities();

        Logger.fine("Loading kits...");
        this.kitFactory = KitFactory.make();
        this.kitFactory.loadKits();

        Logger.fine("Loading players...");
        this.localPlayerFactory = LocalPlayerFactory.make();
        this.localPlayerFactory.loadLocalPlayers();

        getServer().getPluginManager().registerEvents(this, this);
        new AbilityListeners(this);
        new PlayerListeners(this);
        new CommandsRegistry(this);
    }

    @Override
    public void onDisable() {
        Logger.fine("Saving players...");
        localPlayerFactory.saveLocalPlayers();
    }

    public void registerListener(Listener listener) { this.getServer().getPluginManager().registerEvents(listener, singleton); }


    public static KitPlugin getSingleton() { return singleton; }

    public KitFactory getKitRegistry() { return this.kitFactory; }

    public LocalPlayerFactory getPlayerFactory() { return this.localPlayerFactory; }

    public AbilityFactory getAbilityFactory() { return this.abilityFactory; }

    public ConfigurationNode getConf() {
        return conf;
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        final LocalPlayer localPlayer = localPlayerFactory.getLocalPlayer(e.getPlayer());
        if(e.getMessage().startsWith("/kits")) {
            localPlayer.incrementPoints(15);
            localPlayer.openKitSelector();
            e.setCancelled(true);
        }
    }

    /*
     For testing
    */
    public KitPlugin() { super(); }
    protected KitPlugin(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
    }

}
