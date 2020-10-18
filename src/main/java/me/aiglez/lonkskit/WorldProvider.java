package me.aiglez.lonkskit;

import me.lucko.helper.Helper;
import org.bukkit.Bukkit;
import org.bukkit.World;

import javax.annotation.Nonnull;

public class WorldProvider {

    @Nonnull
    public static final World KP_WORLD = Helper.world("kitpvp").orElseThrow(() -> new IllegalStateException("Couldn't find any world matching the name [kitpvp]"));
    public static final World MAIN_WORLD = Bukkit.getWorld("world");



    private WorldProvider() {
        throw new UnsupportedOperationException();
    }
}
