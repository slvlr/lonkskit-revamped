package me.aiglez.lonkskit.players.impl;

import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.players.LocalTeleportation;
import me.lucko.helper.Schedulers;
import org.bukkit.Location;

import java.util.concurrent.TimeUnit;

public class LocalTeleportationImpl implements LocalTeleportation {

    private final LocalPlayer localPlayer;
    private final Location location;

    public LocalTeleportationImpl(LocalPlayer localPlayer, Location location) {
        this.localPlayer = localPlayer;
        this.location = location;
    }

    @Override
    public Location getLocationTo() {
        return location;
    }

    @Override
    public LocalPlayer getTeleported() {
        return localPlayer;
    }

    public void startTeleportion() {
        Schedulers.sync()
                .run(() -> localPlayer.msg("&eTeleporting in {0} second(s), do not move!", 5))
                .thenRunDelayedSync(() -> {
                    localPlayer.msg("&7Teleporating...");
                    localPlayer.toBukkit().teleport(location);
                }, 5, TimeUnit.SECONDS);
    }
}
