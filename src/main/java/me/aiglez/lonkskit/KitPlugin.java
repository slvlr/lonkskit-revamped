package me.aiglez.lonkskit;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
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
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import me.lucko.helper.plugin.ap.Plugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

@Plugin(
        name = "LonksKit", version = "1.0.9",
        authors = "AigleZ", apiVersion = "1.16",
        hardDepends = {"ProtocolLib", "helper"}
)
public final class KitPlugin extends ExtendedJavaPlugin implements Listener {

    private static KitPlugin singleton;

    private KitFactory kitFactory;
    private LocalPlayerFactory localPlayerFactory;
    private AbilityFactory abilityFactory;
    private ConfigurationNode conf;

    private ProtocolManager protocolManager; // protocol lib hook

    @Override
    protected void load() {
        this.protocolManager = ProtocolLibrary.getProtocolManager();
    }

    @Override
    public void enable() {
        singleton = this;
        Logger.fine("Loading configuration files...");
        this.conf = ConfigFactory.yaml().load(getBundledFile("config.yml"));

        Logger.fine("Loading abilities...");
        this.abilityFactory = AbilityFactory.make();
        this.abilityFactory.registerAbilities();

        Logger.fine("Loading kits...");
        this.kitFactory = KitFactory.make();
        this.kitFactory.loadKits();

        Logger.fine("Loading players...");
        this.localPlayerFactory = LocalPlayerFactory.makeInstance();
        this.localPlayerFactory.loadLocalPlayers();

        Logger.fine("Registering listeners and commands...");
        new AbilityListeners(this);
        new PlayerListeners(this);

        getServer().getPluginManager().registerEvents(this, this);

        new CommandsRegistry(this);
    }

    @Override
    public void disable() {
        Logger.fine("Saving players...");
        localPlayerFactory.saveLocalPlayers();
    }

    public static KitPlugin getSingleton() { return singleton; }

    public KitFactory getKitFactory() { return this.kitFactory; }

    public LocalPlayerFactory getPlayerFactory() { return this.localPlayerFactory; }

    public AbilityFactory getAbilityFactory() { return this.abilityFactory; }

    public ProtocolManager getProtocolManager() { return protocolManager; }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        final LocalPlayer localPlayer = localPlayerFactory.getLocalPlayer(e.getPlayer());
        if(e.getMessage().startsWith("/kits")) {
            localPlayer.incrementPoints(15);
            localPlayer.openKitSelector();
            e.setCancelled(true);
        }
    }

}
