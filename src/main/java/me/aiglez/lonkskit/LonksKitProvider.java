package me.aiglez.lonkskit;


import me.aiglez.lonkskit.abilities.factory.AbilityFactory;
import me.aiglez.lonkskit.kits.KitFactory;
import me.aiglez.lonkskit.players.LocalPlayerFactory;

public class LonksKitProvider {

    public static KitFactory getKitRegistry() {
        return KitPlugin.getSingleton().getKitRegistry();
    }

    public static LocalPlayerFactory getPlayerFactory() { return KitPlugin.getSingleton().getPlayerFactory(); }

    public static AbilityFactory getAbilityFactory() {return KitPlugin.getSingleton().getAbilityFactory();}

    public LonksKitProvider() {
        throw new UnsupportedOperationException();
    }
}
