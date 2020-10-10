package me.aiglez.lonkskit.abilities.factory;

import me.aiglez.lonkskit.KitPlugin;
import me.aiglez.lonkskit.abilities.Ability;
import me.aiglez.lonkskit.abilities.external.functionnal.*;
import me.aiglez.lonkskit.abilities.external.itembased.*;
import me.aiglez.lonkskit.abilities.functionnal.SharkAbility;
import me.aiglez.lonkskit.abilities.functionnal.StompAbility;
import me.aiglez.lonkskit.abilities.itembased.*;
import me.aiglez.lonkskit.exceptions.AbilityFileNotFoundException;
import me.aiglez.lonkskit.exceptions.AbilityRegisterException;
import me.lucko.helper.config.ConfigFactory;
import me.lucko.helper.config.ConfigurationNode;
import org.bukkit.Material;

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

        /*Ability portastomp = new PortastompAbility(
                getFileByName("portastomp").orElseThrow(() -> new AbilityFileNotFoundException("portastomp"))
        );
        this.abilities.add(portastomp);*/

        /*Ability wizard = new WizardAbility(
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
        this.abilities.add(blink);*/

        /*Ability kangaroo = new KangarooAbility(
                getFileByName("kangaroo").orElseThrow(() -> new AbilityFileNotFoundException("kangaroo"))
        );
        this.abilities.add(kangaroo);
*/
        /*Ability shark = new SharkAbility(
                getFileByName("shark").orElseThrow(() -> new AbilityFileNotFoundException("shark"))
        );
        this.abilities.add(shark);*/


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

        Ability sonic = new SonicAbility(
                getFileByName("sonic").orElseThrow(() -> new AbilityFileNotFoundException("sonic"))
        );
        this.abilities.add(sonic);

        Ability ghost = new GhostAbility(
                getFileByName("ghost").orElseThrow(() -> new AbilityFileNotFoundException("ghost"))
        );
        this.abilities.add(ghost);

        Ability hyper = new HyperAbility(
                getFileByName("hyper").orElseThrow(() -> new AbilityFileNotFoundException("hyper"))
        );
        this.abilities.add(hyper);

        Ability berserker = new BerserkerAbility(
                getFileByName("berserker").orElseThrow(() -> new AbilityFileNotFoundException("berserker"))
        );
        this.abilities.add(berserker);

        Ability turtle = new TurtleAbility(
                getFileByName("turtle").orElseThrow(() -> new AbilityFileNotFoundException("turtle"))
        );
        this.abilities.add(turtle);

        Ability snake = new SnakeAbility(
                getFileByName("snake").orElseThrow(() -> new AbilityFileNotFoundException("snake"))
        );
        this.abilities.add(snake);

        Ability flamer = new FlamerAbility(
                getFileByName("flamer").orElseThrow(() -> new AbilityFileNotFoundException("flamer"))
        );

        this.abilities.add(flamer);

        Ability killer = new KillerAbility(
                getFileByName("killer").orElseThrow(() -> new AbilityFileNotFoundException("killer"))
        );

        this.abilities.add(killer);

        Ability elder = new ElderAbility(
                getFileByName("elder").orElseThrow(() -> new AbilityFileNotFoundException("elder"))
        );

        this.abilities.add(elder);

        Ability BeastMaster = new BeastmasterAbility(
                getFileByName("beastmaster").orElseThrow(() -> new AbilityFileNotFoundException("beastmaster"))
        );

        this.abilities.add(BeastMaster);



        Ability casper = new Casper(
                getFileByName("casper").orElseThrow(() -> new AbilityFileNotFoundException("casper"))
        );
        this.abilities.add(casper);

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
