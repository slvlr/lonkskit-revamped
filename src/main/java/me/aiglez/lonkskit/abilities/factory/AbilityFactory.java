package me.aiglez.lonkskit.abilities.factory;

import me.aiglez.lonkskit.abilities.Ability;

import java.util.Optional;
import java.util.Set;

public interface AbilityFactory {

    Optional<Ability> getAbility(String name);

    void registerAbilities();

    /**
     * Registers a new ability
     * @param name the ability's name (must no contain spaces, special chars).
     * @param ability the ability instance.
     * @throws me.aiglez.lonkskit.exceptions.AbilityRegisterException if the ability couldn't be registered
     */
    void registerAbility(String name, Ability ability);

    Set<Ability> getAbilities();

    static AbilityFactory make() {
        return new AbilityFactoryImpl();
    }

}
