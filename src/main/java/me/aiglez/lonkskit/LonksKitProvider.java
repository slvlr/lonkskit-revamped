package me.aiglez.lonkskit;


import me.aiglez.lonkskit.abilities.factory.AbilityFactory;
import me.aiglez.lonkskit.kits.KitFactory;
import me.aiglez.lonkskit.players.LocalPlayerFactory;

public class LonksKitProvider {

    public static KitFactory getKitFactory() {
        return KitPlugin.getSingleton().getKitFactory();
    }

    public static LocalPlayerFactory getPlayerFactory() { return KitPlugin.getSingleton().getPlayerFactory(); }

    public static AbilityFactory getAbilityFactory() {return KitPlugin.getSingleton().getAbilityFactory();}

    private LonksKitProvider() {
        throw new UnsupportedOperationException();
    }
}
