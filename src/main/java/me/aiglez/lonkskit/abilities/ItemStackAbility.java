package me.aiglez.lonkskit.abilities;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import me.aiglez.lonkskit.exceptions.AbilityRegisterException;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.Logger;
import me.aiglez.lonkskit.utils.PotionEffectBuilder;
import me.aiglez.lonkskit.utils.items.ItemStackBuilder;
import me.lucko.helper.config.ConfigurationNode;
import me.lucko.helper.config.objectmapping.ObjectMappingException;
import me.lucko.helper.config.yaml.YAMLConfigurationLoader;
import me.lucko.helper.cooldown.Cooldown;
import me.lucko.helper.cooldown.CooldownMap;
import me.lucko.helper.function.chain.Chain;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public abstract class ItemStackAbility implements Ability, Listener {

    private final ItemStack item;
    public static final List<Material> ABILITY_ITEMS = new ArrayList<>();
    protected final String name;
    protected final CooldownMap<LocalPlayer> cooldown;
    protected final Set<PotionEffect> potionEffects;
    private final YAMLConfigurationLoader configurationLoader;
    protected ConfigurationNode configuration;

    protected ItemStackAbility(String name, YAMLConfigurationLoader configurationLoader) throws IOException {
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(configurationLoader);
        this.name = name;
        this.configurationLoader = configurationLoader;

        if(configurationLoader.canLoad()) {
            this.configuration = configurationLoader.load();
        } else {
            throw new AbilityRegisterException(name, "Can't load ability's file");
        }

        int cooldownSeconds = configuration.getNode("cooldown").getInt(0);
        this.cooldown = CooldownMap.create(Cooldown.of(cooldownSeconds, TimeUnit.SECONDS));

        try {
            this.potionEffects = Chain.start(configuration.getNode("potion-effects").getList(new TypeToken<String>() {}))
                    .map(list -> list.stream().map(unparsed -> PotionEffectBuilder.parse(unparsed).build()).filter(Objects::nonNull)
                            .collect(Collectors.toSet()))
                    .end().orElse(Collections.emptySet());
        } catch (ObjectMappingException e) {
            throw new AbilityRegisterException(name, "Couldn't map an object (LIST) // " + e.getMessage());
        }

        try {
            final Map<Enchantment, Integer> enchants = Maps.newHashMap();

            configuration.getNode("item", "enchantments").getList(new TypeToken<String>() {}).forEach(unparsed -> {

                final String[] enchantSplit = unparsed.split(":");
                if(enchantSplit == null || enchantSplit.length < 2) {
                    return;
                }

                final Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchantSplit[0].toLowerCase()));
                final int level = NumberUtils.toInt(enchantSplit[1], 1);
                if (enchantment == null) {
                    Logger.severe("Couldn't find any enchantement with name `" + enchantSplit[0] + "`");
                    return;
                }
                enchants.put(enchantment, level);

            });

            this.item = ItemStackBuilder.of(Material.matchMaterial(configuration.getNode("item", "material").getString("STONE")))
                    .name(configuration.getNode("item", "name").getString("Name"))
                    .lore(configuration.getNode("item", "lore").getList(new TypeToken<String>() {}))
                    .amount(configuration.getNode("item", "amount").getInt(1))
                    .apply(builder -> enchants.forEach(builder::enchant))
                    .breakable(false)
                    .build();
            ABILITY_ITEMS.add(this.item.getType());

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
     * Ability's itemstack.
     * @return the itemstack representing the ability
     */
    public ItemStack getItemStack() { return this.item; }

    /**
     * Whether the itemstack is representing this ability
     * @param item the itemstack
     * @return true if the itemstack is representing this ability
     */
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
                .append("itembased", true)
                .toString();
    }
}
