package me.aiglez.lonkskit;

import me.aiglez.lonkskit.abilities.factory.AbilityFactory;
import me.aiglez.lonkskit.commands.CommandsRegistry;
import me.aiglez.lonkskit.commands.KitCommand;
import me.aiglez.lonkskit.controllers.Controllers;
import me.aiglez.lonkskit.kits.KitFactory;
import me.aiglez.lonkskit.listeners.*;
import me.aiglez.lonkskit.players.LocalPlayerFactory;
import me.aiglez.lonkskit.struct.ghost.GhostFactory;
import me.aiglez.lonkskit.utils.Logger;
import me.lucko.helper.Commands;
import me.lucko.helper.command.Command;
import me.lucko.helper.config.ConfigurationNode;
import me.lucko.helper.hologram.Hologram;
import me.lucko.helper.hologram.HologramFactory;
import me.lucko.helper.maven.MavenLibrary;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import me.lucko.helper.plugin.ap.Plugin;
import me.lucko.helper.serialize.Position;
import me.lucko.helper.signprompt.SignPromptFactory;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@Plugin(
        name = "LonksKit", version  = "1.0.9",
        authors = {"AigleZ","Johan"}, apiVersion = "1.16",
        hardDepends = {"ProtocolLib", "helper", "LibsDisguises", "HolographicDisplays"}
)
public final class KitPlugin extends ExtendedJavaPlugin {

    private static KitPlugin singleton;
    private boolean loaded;

    private KitFactory kitFactory;
    private LocalPlayerFactory localPlayerFactory;
    private AbilityFactory abilityFactory;

    private ConfigurationNode conf, lang;

    @Override
    public void enable() {
        singleton = this;
        Logger.fine("Loading configuration files...");
        this.conf = loadConfigNode("config.yml");
        this.lang = loadConfigNode("messages.yml");
        this.abilityFactory = AbilityFactory.make();
        this.kitFactory = KitFactory.make();
        this.localPlayerFactory = LocalPlayerFactory.make();
        provideService(KitPlugin.class,this, ServicePriority.Highest);
        Logger.fine("Loading abilities...");
        this.abilityFactory.registerAbilities();

        Logger.fine("Loading kits...");
        this.kitFactory.loadKits();

        Logger.fine("Loading players...");
        this.localPlayerFactory.loadOfflineLocalPlayers();

        Logger.fine("Registering listeners and commands...");
        registerListeners();
        registerCommands();
        Logger.fine("Initializing controllers...");
        Controllers.initControllers();

        loaded = true;
        new GhostFactory(this);
        Commands.create().assertPlayer().handler(c -> {
            if (!c.sender().isGlowing()){
                c.sender().setGlowing(true);
                c.sender().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,200,666,true,false,false));
            }else {
                c.sender().setGlowing(false);
                c.sender().getActivePotionEffects().clear();
            }
        }).register("testH");

    }

    @Override
    public void disable() {
        if(!loaded) {
            Logger.severe("Shutting down, the plugin didn't load properly!");
            return;
        }
        Logger.fine("Saving players...");
        localPlayerFactory.saveOfflineLocalPlayers();
    }

    private void registerListeners() {
        new AbilityListeners(this);
        new InteractListeners(this);
        new PlayerListeners(this);
        new FeaturesListeners(this);
        new DisconnectListener(this);
        new CombatListeners(this);
    }

    private void registerCommands() {
        new CommandsRegistry(this);
        new KitCommand(this);
    }

    public static KitPlugin getSingleton() { return singleton; }

    public KitFactory getKitFactory() { return this.kitFactory; }

    public LocalPlayerFactory getPlayerFactory() { return this.localPlayerFactory; }

    public AbilityFactory getAbilityFactory() { return this.abilityFactory; }

    public ConfigurationNode getConf() { return conf; }

    public ConfigurationNode getLang() { return lang; }
}
