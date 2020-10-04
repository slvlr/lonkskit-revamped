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


    public abstract ItemStack getItemStack();

    public abstract boolean isItemStack(ItemStack item);

    public abstract void whenUsed(PlayerInteractEvent e);



    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", name)
                .append("itembased", true)
                .toString();
    }
}
