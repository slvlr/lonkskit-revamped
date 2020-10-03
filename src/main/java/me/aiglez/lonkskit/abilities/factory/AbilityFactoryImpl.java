package me.aiglez.lonkskit.abilities.factory;

import me.aiglez.lonkskit.KitPlugin;
import me.aiglez.lonkskit.abilities.Ability;
import me.aiglez.lonkskit.abilities.external.itembased.MonkAbility;
import me.aiglez.lonkskit.abilities.functionnal.StompAbility;
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

        // Imad - start
        Ability monk = new MonkAbility(
                getFileByName("monk").orElseThrow(() -> new AbilityFileNotFoundException("monk"))
        );
        this.abilities.add(monk);
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
