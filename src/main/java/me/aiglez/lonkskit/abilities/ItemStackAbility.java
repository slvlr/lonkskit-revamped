package me.aiglez.lonkskit.abilities;

import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;
import me.aiglez.lonkskit.exceptions.AbilityRegisterException;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.PotionEffectBuilder;
import me.lucko.helper.config.ConfigurationNode;
import me.lucko.helper.config.objectmapping.ObjectMappingException;
import me.lucko.helper.cooldown.Cooldown;
import me.lucko.helper.cooldown.CooldownMap;
import me.lucko.helper.function.chain.Chain;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public abstract class ItemStackAbility implements Ability, Listener {

    protected ItemStack item;
    protected final String name;
    protected final ConfigurationNode configuration;
    protected final CooldownMap<LocalPlayer> cooldown;
    protected final Set<PotionEffect> potionEffects;

    protected ItemStackAbility(String name, ConfigurationNode configuration) {
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(configuration);
        this.name = name;
        this.configuration = configuration;

        int cooldownSeconds = configuration.getNode("cooldown").getInt(0);
        this.cooldown = CooldownMap.create(Cooldown.of(cooldownSeconds, TimeUnit.SECONDS));

        try {
            this.potionEffects = Chain.start(configuration.getNode("potion-effects").getList(new TypeToken<String>() {}))
                    .map(list -> list.stream().map(unparsed -> PotionEffectBuilder.parse(unparsed).build()).filter(Objects::nonNull).collect(Collectors.toSet()))
                    .orElseIfNull(Collections.emptySet()).endOrNull();
        } catch (ObjectMappingException e) {
            throw new AbilityRegisterException(name, "Couldn't map an object (LIST) // " + e.getMessage());
        }

        registerListeners();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public ConfigurationNode getConfiguration() {
        return this.configuration;
    }

    @Override
    public CooldownMap<LocalPlayer> getCooldown() {
        return this.cooldown;
    }

    @Override
    public void reloadConfiguration() {
        // TODO: implement this
    }

    public ItemStack getItemStack() { return this.item; }

    public boolean isItemStack(ItemStack item) {
        Preconditions.checkNotNull(item);
        if(this.item == null) return false;
        return this.item.isSimilar(item);
    }

    /*
     * Called when a player right-clicks on the itemstack.
     */
    public abstract void whenRightClicked(PlayerInteractEvent e);

    /*
     * Called when a player left-clicks on the itemstack.
     */
    public abstract void whenLeftClicked(PlayerInteractEvent e);

    @Override
    public void applyEffects(LocalPlayer localPlayer) {

    }

    @Override
    public void removeEffects(LocalPlayer localPlayer) {

    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", name)
                .append("itembased", true)
                .toString();
    }
}
