package me.aiglez.lonkskit.abilities.factory;

import me.aiglez.lonkskit.KitPlugin;
import me.aiglez.lonkskit.abilities.Ability;
import me.aiglez.lonkskit.abilities.functional.*;
import me.aiglez.lonkskit.abilities.itembased.*;
import me.aiglez.lonkskit.exceptions.AbilityFileNotFoundException;
import me.aiglez.lonkskit.exceptions.AbilityRegisterException;
import me.aiglez.lonkskit.utils.Logger;
import me.lucko.helper.config.ConfigFactory;
import me.lucko.helper.config.ConfigurationNode;
import org.bukkit.ChatColor;

import java.io.File;
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
        registerAbility("thor", new ThorAbility(getFileByName("thor")));
        registerAbility("dragon", new DragonAbility(getFileByName("dragon")));
        registerAbility("switcher", new SwitcherAbility(getFileByName("switcher")));
        registerAbility("portastomp", new PortastompAbility(getFileByName("portastomp")));
        registerAbility("wizard", new WizardAbility(getFileByName("wizard")));
        registerAbility("stomp", new StompAbility(getFileByName("stomp")));
        registerAbility("troll", new TrollAbility(getFileByName("troll")));
        registerAbility("blink", new BlinkAbility(getFileByName("blink")));
        registerAbility("kangaroo", new KangarooAbility(getFileByName("kangaroo")));
        registerAbility("shark", new SharkAbility(getFileByName("shark")));
        registerAbility("jedi", new JediAbility(getFileByName("jedi")));
        registerAbility("chomp", new ChompAbility(getFileByName("chomp")));
        registerAbility("shooter", new ShooterAbility(getFileByName("shooter")));
        registerAbility("superman", new SupermanAbility(getFileByName("superman")));
        registerAbility("fisherman", new FishermanAbility(getFileByName("fisherman")));
        registerAbility("hooker", new HookerAbility(getFileByName("hooker")));
        registerAbility("tiger", new TigerAbility(getFileByName("tiger")));
        registerAbility("spy", new SpyAbility(getFileByName("spy")));

        // BETA
        registerAbility("cowboy", new CowboyAbility(getFileByName("cowboy")));
        registerAbility("spy", new SpyAbility(getFileByName("spy")));
        registerAbility("hothead", new HotheadAbility(getFileByName("hothead")));
        registerAbility("hulk", new HulkAbility(getFileByName("hulk")));
        registerAbility("flinger", new FlingerAbility(getFileByName("flinger")));
        registerAbility("hothead", new HotheadAbility(getFileByName("hothead")));
        registerAbility("kamikaze", new KamikazeAbility(getFileByName("kamikaze")));


        registerAbility("wallbuilder", new WallBuilderAbility(getFileByName("wallbuilder")));

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
}
