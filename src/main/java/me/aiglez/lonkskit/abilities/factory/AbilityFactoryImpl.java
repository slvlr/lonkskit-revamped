package me.aiglez.lonkskit.abilities.factory;

import me.aiglez.lonkskit.KitPlugin;
import me.aiglez.lonkskit.abilities.Ability;
import me.aiglez.lonkskit.abilities.functional.*;
import me.aiglez.lonkskit.abilities.itembased.*;
import me.aiglez.lonkskit.exceptions.AbilityFileNotFoundException;
import me.aiglez.lonkskit.exceptions.AbilityRegisterException;
import me.aiglez.lonkskit.utils.Logger;
import me.lucko.helper.Services;
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
        long ms = System.currentTimeMillis();
        int aiglez = 0, johan = 0;
        try {
            Logger.debug("[Abilities] registering functional abilities...");
            registerAbility("hothead", new HotheadAbility(yamlConfigurationLoader("hothead")));
            registerAbility("hulk", new HulkAbility(yamlConfigurationLoader("hulk")));
            registerAbility("shark", new SharkAbility(yamlConfigurationLoader("shark")));
            registerAbility("shooter", new ShooterAbility(yamlConfigurationLoader("shooter")));
            registerAbility("shark", new SharkAbility(yamlConfigurationLoader("shark")));
            registerAbility("stomp", new StompAbility(yamlConfigurationLoader("stomp")));
            aiglez += 6;

            // Johan - start
            registerAbility("anvil", new AnvilAbility(yamlConfigurationLoader("anvil")));
            registerAbility("berserker", new BerserkerAbility(yamlConfigurationLoader("berserker")));
            //registerAbility("disguises", new DisguiseAbilities(yamlConfigurationLoader("disguises")));
            registerAbility("flamer", new FlamerAbility(yamlConfigurationLoader("flamer")));
            registerAbility("killer", new KillerAbility(yamlConfigurationLoader("killer")));
            registerAbility("recall", new RecallAbility(yamlConfigurationLoader("recall")));
            registerAbility("turtle", new TurtleAbility(yamlConfigurationLoader("turtle")));
            //registerAbility("spider",new SpiderAbility(yamlConfigurationLoader("spider")));
            registerAbility("cowboy",new CowboyAbility(yamlConfigurationLoader("cowboy")));
            TeleAbility teleAbility = new TeleAbility(yamlConfigurationLoader("tele"));
            registerAbility("tele",teleAbility);
            registerAbility("assassin",new Assassin(yamlConfigurationLoader("assassin")));
            //registerAbility("snowman",new SnowmanAbility(yamlConfigurationLoader("snowman")));
            //registerAbility("creeper",new CreeperAbility(yamlConfigurationLoader("creeper")));
            //registerAbility("mooshroom",new MooshroomAbility(yamlConfigurationLoader("mooshroom")));
            registerAbility("spartan",new SpartanAbility(yamlConfigurationLoader("spartan")));
            registerAbility("demoman",new DemomanAbility(yamlConfigurationLoader("demoman")));
            johan += 15;
            // Johan - end

            Logger.debug("[Abilities] registering itemstack abilities...");
            registerAbility("blink", new BlinkAbility(yamlConfigurationLoader("blink")));
            registerAbility("chomp", new ChompAbility(yamlConfigurationLoader("chomp")));
            //registerAbility("cowboy", new CowboyAbility(yamlConfigurationLoader("cowboy")));
            registerAbility("dragon", new DragonAbility(yamlConfigurationLoader("dragon")));
            registerAbility("fisherman", new FishermanAbility(yamlConfigurationLoader("fisherman")));
            registerAbility("flinger", new FlingerAbility(yamlConfigurationLoader("flinger")));
            registerAbility("hooker", new HookerAbility(yamlConfigurationLoader("hooker")));
            registerAbility("jedi", new JediAbility(yamlConfigurationLoader("jedi")));
            registerAbility("kamikaze", new KamikazeAbility(yamlConfigurationLoader("kamikaze")));
            registerAbility("kangaroo", new KangarooAbility(yamlConfigurationLoader("kangaroo")));
            registerAbility("portastomp", new PortastompAbility(yamlConfigurationLoader("portastomp")));
            registerAbility("superman", new SupermanAbility(yamlConfigurationLoader("superman")));
            registerAbility("switcher", new SwitcherAbility(yamlConfigurationLoader("switcher")));
            registerAbility("thor", new ThorAbility(yamlConfigurationLoader("thor")));
            registerAbility("tiger", new TigerAbility(yamlConfigurationLoader("tiger")));
            registerAbility("troll", new TrollAbility(yamlConfigurationLoader("troll")));
            registerAbility("wizard", new WizardAbility(yamlConfigurationLoader("wizard")));
            registerAbility("spy", new SpyAbility(yamlConfigurationLoader("spy")));
            registerAbility("wallbuilder", new WallBuilderAbility(yamlConfigurationLoader("wallbuilder")));
            registerAbility("domebuilder", new DomeBuilderAbility(yamlConfigurationLoader("domebuilder")));
            aiglez += 19;

            // johan - start
            registerAbility("elder", new ElderAbility(yamlConfigurationLoader("elder")));
            registerAbility("ghost", new GhostAbility(yamlConfigurationLoader("ghost")));
            registerAbility("hyper", new HyperAbility(yamlConfigurationLoader("hyper")));
            registerAbility("monk", new MonkAbility(yamlConfigurationLoader("monk")));
            registerAbility("shadowblade", new ShadowbladeAbility(yamlConfigurationLoader("shadowblade")));
            registerAbility("snake", new SnakeAbility(yamlConfigurationLoader("snake")));
            registerAbility("wraith", new WraithAbility(yamlConfigurationLoader("wraith")));
            registerAbility("sonic", new SonicAbility(yamlConfigurationLoader("sonic")));
            registerAbility("mortar",new MortarAbility(yamlConfigurationLoader("mortar")));
            johan += 8;
            // johan - end
            Services.provide(TeleAbility.class,teleAbility);
        } catch (AbilityRegisterException | AbilityFileNotFoundException | IOException e) {
            e.printStackTrace();
        }

        Logger.fine(ChatColor.GREEN + "Registered a total of {0} abilities. (aiglez: {1} | johan: {2})",
                abilities.size(), aiglez, johan
        );
        Logger.fine(ChatColor.DARK_GREEN + "Took " + ((float) System.currentTimeMillis() - ms) + " ms");
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

    public static ConfigurationNode getFileByName(String name) {
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
