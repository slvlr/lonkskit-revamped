package me.aiglez.lonkskit.abilities;

import com.google.common.base.Preconditions;
import me.aiglez.lonkskit.LonksKitProvider;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.lucko.helper.config.ConfigurationNode;
import me.lucko.helper.cooldown.CooldownMap;

public interface Ability {

    String getName();

    ConfigurationNode getConfiguration();

    CooldownMap<LocalPlayer> getCooldown();

    void handleListeners();

    static Ability get(String name) {
        Preconditions.checkNotNull(name, "ability name may not be null");
        return LonksKitProvider.getAbilityFactory().getAbility(name).orElseThrow(() -> new NullPointerException("ability with name " + name + " not found:"));
    }

    enum Type {

        FUNCTIONNAL,
        ITEMSTACK;

    }
}
