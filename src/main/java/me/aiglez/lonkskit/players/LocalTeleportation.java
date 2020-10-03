package me.aiglez.lonkskit.players;

import me.aiglez.lonkskit.players.impl.LocalTeleportationImpl;
import org.bukkit.Location;

public interface LocalTeleportation {

    Location getLocationTo();

    LocalPlayer getTeleported();

    static LocalTeleportation make(LocalPlayer localPlayer, Location location) {
        return new LocalTeleportationImpl(localPlayer, location);
    }
}
