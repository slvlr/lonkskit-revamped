package me.aiglez.lonkskit;

import com.google.common.base.Preconditions;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.lucko.helper.Helper;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import javax.annotation.Nonnull;

public class WorldProvider {

    @Nonnull
    public static final World KP_WORLD = Helper.world("kitpvp").orElse(Bukkit.createWorld(new WorldCreator("kitpvp")));
    public static final World MAIN_WORLD = Bukkit.getWorld("world");
    public static final World MAIN = Helper.world("towny").orElse(Helper.world("Towny").orElse(Bukkit.createWorld(new WorldCreator("Towny"))));

    public static boolean inKPWorld(LocalPlayer localPlayer) {
        Preconditions.checkNotNull(localPlayer, "local player may not be null");
        return localPlayer.isOnline() && localPlayer.getWorld().getUID().equals(KP_WORLD.getUID());
    }

    private WorldProvider() {
        throw new UnsupportedOperationException();
    }
}
