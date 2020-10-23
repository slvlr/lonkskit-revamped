package me.aiglez.lonkskit.players;

import me.aiglez.lonkskit.players.impl.MemoryLocalMetrics;

@SuppressWarnings({"unused", "RedundantSuppression"})
public interface LocalMetrics {

    LocalPlayer getHolder();

    int getKillsCount();

    void incrementKillsCount();

    int getDeathsCount();

    void incrementDeathsCount();

    double getKDR();

    void updateAll(int kills, int deaths);

    static LocalMetrics newMetrics(LocalPlayer localPlayer) {
        return new MemoryLocalMetrics(localPlayer);
    }
}
