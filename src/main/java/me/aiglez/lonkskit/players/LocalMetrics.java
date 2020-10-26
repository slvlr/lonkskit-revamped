package me.aiglez.lonkskit.players;

import me.aiglez.lonkskit.players.impl.MemoryLocalMetrics;

@SuppressWarnings({"unused", "RedundantSuppression"})
public interface LocalMetrics {

    OfflineLocalPlayer getHolder();

    int getKillsCount();

    void incrementKillsCount();

    int getDeathsCount();

    void incrementDeathsCount();

    double getKDR();

    void updateAll(int kills, int deaths);

    static LocalMetrics newMetrics(OfflineLocalPlayer offlineLocalPlayer) {
        return new MemoryLocalMetrics(offlineLocalPlayer);
    }
}
