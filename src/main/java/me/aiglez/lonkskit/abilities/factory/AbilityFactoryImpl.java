package me.aiglez.lonkskit.abilities.factory;

import me.aiglez.lonkskit.KitPlugin;
import me.aiglez.lonkskit.abilities.Ability;
import me.aiglez.lonkskit.abilities.external.functionnal.AnvilAbility;
import me.aiglez.lonkskit.abilities.external.itembased.MonkAbility;
import me.aiglez.lonkskit.abilities.external.itembased.ShadowbladeAbility;
import me.aiglez.lonkskit.abilities.functional.SharkAbility;
import me.aiglez.lonkskit.abilities.functional.ShooterAbility;
import me.aiglez.lonkskit.abilities.functional.StompAbility;
import me.aiglez.lonkskit.abilities.itembased.*;
import me.aiglez.lonkskit.exceptions.AbilityFileNotFoundException;
import me.aiglez.lonkskit.exceptions.AbilityRegisterException;
import me.lucko.helper.config.ConfigFactory;
import me.lucko.helper.config.ConfigurationNode;

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
        Ability thor = new ThorAbility(
                getFileByName("thor").orElseThrow(() -> new AbilityFileNotFoundException("thor"))
        );
        this.abilities.add(thor);

        Ability dragon = new DragonAbility(
                getFileByName("dragon").orElseThrow(() -> new AbilityFileNotFoundException("dragon"))
        );
        this.abilities.add(dragon);

        Ability switcher = new SwitcherAbility(
                getFileByName("switcher").orElseThrow(() -> new AbilityFileNotFoundException("switcher"))
        );
        this.abilities.add(switcher);

        Ability portastomp = new PortastompAbility(
                getFileByName("portastomp").orElseThrow(() -> new AbilityFileNotFoundException("portastomp"))
        );
        this.abilities.add(portastomp);

        Ability wizard = new WizardAbility(
                getFileByName("wizard").orElseThrow(() -> new AbilityFileNotFoundException("wizard"))
        );
        this.abilities.add(wizard);

        Ability stomp = new StompAbility(
                getFileByName("stomp").orElseThrow(() -> new AbilityFileNotFoundException("stomp"))
        );
        this.abilities.add(stomp);

        Ability troll = new TrollAbility(
                getFileByName("troll").orElseThrow(() -> new AbilityFileNotFoundException("troll"))
        );
        this.abilities.add(troll);

        Ability blink = new BlinkAbility(
                getFileByName("blink").orElseThrow(() -> new AbilityFileNotFoundException("blink"))
        );
        this.abilities.add(blink);

        Ability kangaroo = new KangarooAbility(
                getFileByName("kangaroo").orElseThrow(() -> new AbilityFileNotFoundException("kangaroo"))
        );
        this.abilities.add(kangaroo);

        Ability shark = new SharkAbility(
                getFileByName("shark").orElseThrow(() -> new AbilityFileNotFoundException("shark"))
        );
        this.abilities.add(shark);

        Ability jedi = new JediAbility(
                getFileByName("jedi").orElseThrow(() -> new AbilityFileNotFoundException("jedi"))
        );
        this.abilities.add(jedi);

        Ability chomp = new ChompAbility(
                getFileByName("chomp").orElseThrow(() -> new AbilityFileNotFoundException("chomp"))
        );
        this.abilities.add(chomp);

        Ability shooter = new ShooterAbility(
                getFileByName("shooter").orElseThrow(() -> new AbilityFileNotFoundException("shooter"))
        );
        this.abilities.add(shooter);

        Ability superman = new SupermanAbility(
                getFileByName("superman").orElseThrow(() -> new AbilityFileNotFoundException("superman"))
        );
        this.abilities.add(superman);

        Ability cowboy = new CowboyAbility(
                getFileByName("cowboy").orElseThrow(() -> new AbilityFileNotFoundException("cowboy"))
        );
        this.abilities.add(cowboy);
        // Imad - start
        Ability monk = new MonkAbility(
                getFileByName("monk").orElseThrow(() -> new AbilityFileNotFoundException("monk"))
        );
        this.abilities.add(monk);

        Ability anvil = new AnvilAbility(
                getFileByName("anvil").orElseThrow(() -> new AbilityFileNotFoundException("anvil"))
        );
        this.abilities.add(anvil);

        Ability shadowblade = new ShadowbladeAbility(
                getFileByName("shadowblade").orElseThrow(() -> new AbilityFileNotFoundException("shadowblade"))
        );
        this.abilities.add(shadowblade);

        // Imad - end
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

    private Optional<ConfigurationNode> getFileByName(String name) {
        final File abilityFile = new File(KitPlugin.getSingleton().getDataFolder() + File.separator + "abilities", name + ".yml");
        if(!abilityFile.exists()) return Optional.empty();
        return Optional.of(ConfigFactory.yaml().load(abilityFile));
    }
}
