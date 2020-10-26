package me.aiglez.lonkskit.players;

import me.aiglez.lonkskit.players.impl.LocalPlayerFactoryImpl;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface LocalPlayerFactory {

    OfflineLocalPlayer getOfflineLocalPlayer(UUID uniqueId);

    Optional<OfflineLocalPlayer> getOfflineLocalPlayer(String name);

    LocalPlayer getLocalPlayer(Player player);

    Set<OfflineLocalPlayer> getCachedOfflinePlayers();

    boolean loadOfflineLocalPlayers();

    boolean saveOfflineLocalPlayers();

    static LocalPlayerFactory make() {
        return new LocalPlayerFactoryImpl();
    }
}
