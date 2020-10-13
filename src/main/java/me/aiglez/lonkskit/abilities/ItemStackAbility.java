package me.aiglez.lonkskit.abilities;

import com.google.common.base.Preconditions;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.lucko.helper.config.ConfigurationNode;
import me.lucko.helper.cooldown.Cooldown;
import me.lucko.helper.cooldown.CooldownMap;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.TimeUnit;

public abstract class ItemStackAbility implements Ability, Listener {

    protected ItemStack item;
    protected final String name;
    protected final ConfigurationNode configuration;
    protected final CooldownMap<LocalPlayer> cooldown;

    protected ItemStackAbility(String name, ConfigurationNode configuration) {
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(configuration);
        this.name = name;
        this.configuration = configuration;

        final int cooldownSeconds = configuration.getNode("cooldown").getInt(0);
        this.cooldown = CooldownMap.create(Cooldown.of(cooldownSeconds, TimeUnit.SECONDS));

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
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", name)
                .append("itembased", true)
                .toString();
    }
}
