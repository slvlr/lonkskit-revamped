package me.aiglez.lonkskit.abilities.factory;

import me.aiglez.lonkskit.KitPlugin;
import me.aiglez.lonkskit.abilities.Ability;
import me.aiglez.lonkskit.abilities.functional.HotheadAbility;
import me.aiglez.lonkskit.abilities.functional.HulkAbility;
import me.aiglez.lonkskit.abilities.functional.SharkAbility;
import me.aiglez.lonkskit.abilities.functional.ShooterAbility;
import me.aiglez.lonkskit.exceptions.AbilityFileNotFoundException;
import me.aiglez.lonkskit.exceptions.AbilityRegisterException;
import me.aiglez.lonkskit.utils.Logger;
import me.lucko.helper.config.ConfigFactory;
import me.lucko.helper.config.ConfigurationNode;
import me.lucko.helper.config.yaml.YAMLConfigurationLoader;
import org.bukkit.ChatColor;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class AbilityFactoryImpl implements AbilityFactory {

    private final Set<Ability> abilities;

    public AbilityFactoryImpl() {
        this.abilities = new HashSet<>();
    }

    @Override
    public Optional<Ability> getAbility(String name) { return this.abilities.stream().filter(ability -> ability.getName().equalsIgnoreCase(name)).findAny(); }

    @Override
    public Set<Ability> getAbilities() {
        return Collections.unmodifiableSet(abilities);
    }

    @Override
    public void registerAbilities() {
        try {
            Logger.debug("[Abilities] registering functional abilities...");
            registerAbility("hothead", new HotheadAbility(yamlConfigurationLoader("hothead")));
            registerAbility("hulk", new HulkAbility(yamlConfigurationLoader("hulk")));
            registerAbility("shark", new SharkAbility(yamlConfigurationLoader("shark")));
            registerAbility("shooter", new ShooterAbility(yamlConfigurationLoader("shooter")));
            registerAbility("shark", new SharkAbility(yamlConfigurationLoader("shark")));


            /*
            Logger.debug("Loading Imad's abilities...");
            registerAbility("monk", new MonkAbility(getFileByName("monk")));
            registerAbility("anvil", new AnvilAbility(getFileByName("anvil")));
            registerAbility("shadowblade", new ShadowbladeAbility(getFileByName("shadowblade")));

            registerAbility("sonic", new SonicAbility(getFileByName("sonic")));
            registerAbility("ghost", new GhostAbility(getFileByName("ghost")));
            registerAbility("hyper", new HyperAbility(getFileByName("hyper")));

            registerAbility("berserker", new BerserkerAbility(getFileByName("berserker")));
            registerAbility("turtle", new TurtleAbility(getFileByName("turtle")));
            registerAbility("snake", new SnakeAbility(getFileByName("snake")));

            registerAbility("flamer", new FlamerAbility(getFileByName("flamer")));
            registerAbility("killer", new KillerAbility(getFileByName("killer")));
            registerAbility("elder", new ElderAbility(getFileByName("elder")));

            registerAbility("wolf", new WolfAbility(getFileByName("wolf")));
            registerAbility("spider", new SpiderDAbility(getFileByName("spider")));
            registerAbility("casper", new Casper(getFileByName("casper")));
             */

        } catch (AbilityRegisterException | AbilityFileNotFoundException | IOException e) {
            e.printStackTrace();
        }

        Logger.fine(ChatColor.GREEN + "Registered a total of " + abilities.size() + " abilities.");
    }

    @Override
    public void registerAbility(String name, Ability ability) {
        if(ability == null) {
            throw new AbilityRegisterException((name != null ? name : "unknown/null"), "Ability instance is null.");
        }
        if(!this.abilities.add(ability)) {
            throw new AbilityRegisterException((name != null ? name : "unknown/null"), "Ability with that name already has been registered.");
        }
    }

    private ConfigurationNode getFileByName(String name) {
        final File abilityFile = new File(KitPlugin.getSingleton().getDataFolder() + File.separator + "abilities", name + ".yml");
        if(!abilityFile.exists()) {
            throw new AbilityFileNotFoundException(name);
        }
        return ConfigFactory.yaml().load(abilityFile);
    }

    private YAMLConfigurationLoader yamlConfigurationLoader(String name) {
        final File file = new File(KitPlugin.getSingleton().getDataFolder() + File.separator + "abilities", name + ".yml");
        if(!file.exists()) {
            throw new AbilityFileNotFoundException(name);
        }
        return ConfigFactory.yaml().loader(file);
    }
}
