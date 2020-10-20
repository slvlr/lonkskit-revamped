package me.aiglez.lonkskit.abilities;

import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;
import me.aiglez.lonkskit.exceptions.AbilityRegisterException;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.Logger;
import me.aiglez.lonkskit.utils.PotionEffectBuilder;
import me.lucko.helper.config.ConfigurationNode;
import me.lucko.helper.config.objectmapping.ObjectMappingException;
import me.lucko.helper.config.yaml.YAMLConfigurationLoader;
import me.lucko.helper.cooldown.Cooldown;
import me.lucko.helper.cooldown.CooldownMap;
import me.lucko.helper.function.chain.Chain;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.bukkit.potion.PotionEffect;

import java.io.IOException;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public abstract class FunctionalAbility implements Ability {

    protected final String name;
    protected final CooldownMap<LocalPlayer> cooldown;
    protected final Set<PotionEffect> potionEffects;

    private final YAMLConfigurationLoader configurationLoader;
    protected ConfigurationNode configuration;

    protected FunctionalAbility(String name, YAMLConfigurationLoader configurationLoader) throws IOException {
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(configurationLoader);
        this.name = name;
        this.configurationLoader = configurationLoader;

        if(configurationLoader.canLoad()) {
            this.configuration = configurationLoader.load();
        } else {
            throw new AbilityRegisterException(name, "Can't load ability's file");
        }

        final int cooldownSeconds = configuration.getNode("cooldown").getInt(0);
        this.cooldown = CooldownMap.create(Cooldown.of(cooldownSeconds, TimeUnit.SECONDS));

        try {
            this.potionEffects = Chain.start(configuration.getNode("potion-effects").getList(new TypeToken<String>() {}))
                    .map(list -> list.stream().map(unparsed -> PotionEffectBuilder.parse(unparsed).build()).filter(Objects::nonNull)
                            .collect(Collectors.toSet()))
                    .end().orElse(Collections.emptySet());
        } catch (ObjectMappingException e) {
            throw new AbilityRegisterException(name, "Couldn't map an object (LIST) // " + e.getMessage());
        }

        registerListeners();
    }

    /**
     * Ability's name.
     * @return the name of the ability
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Ability's configuration.
     * @return the configuration of the ability
     */
    @Override
    public ConfigurationNode getConfiguration() {
        return this.configuration;
    }

    /**
     * Ability's cooldown.
     * @return the cooldown of the ability
     */
    @Override
    public CooldownMap<LocalPlayer> getCooldown() {
        return this.cooldown;
    }

    /**
     * Reload the configuration
     * @throws IOException if the file isn't found, or corrupted.
     */
    @Override
    public void reloadConfiguration() throws IOException {
        Logger.debug("[{0}] Reloading configuration ...", this.name);
        if(!this.configurationLoader.canLoad()) {
            Logger.debug("[{0}] Couldn't reload the configuration ...", this.name);
            return;
        }
        this.configuration = this.configurationLoader.load();
    }

    /**
     * Apply potion effects to the given local player.
     * @param localPlayer local player
     */
    @Override
    public void applyEffects(LocalPlayer localPlayer) {
        localPlayer.toBukkit().addPotionEffects(this.potionEffects);
    }

    /**
     * Remove potion effects from the given local player.
     * @param localPlayer local player
     */
    @Override
    public void removeEffects(LocalPlayer localPlayer) {
        this.potionEffects.forEach(potionEffect -> localPlayer.toBukkit().removePotionEffect(potionEffect.getType()));
    }

    /**
     * Whether the given local player has the ability's potion effects.
     * @param localPlayer local player
     * @return true if the given local player has the ability's potion effects
     */
    @Override
    public boolean hasEffects(LocalPlayer localPlayer) {
        boolean has = false;
        for (final PotionEffect pe : potionEffects) {
            if(!localPlayer.toBukkit().hasPotionEffect(pe.getType())) {
                return false;
            }
            has = true;
        }
        return has;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", name)
                .toString();
    }
}
