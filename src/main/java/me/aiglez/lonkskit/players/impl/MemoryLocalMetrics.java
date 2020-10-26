package me.aiglez.lonkskit.players.impl;

import me.aiglez.lonkskit.players.LocalMetrics;
import me.aiglez.lonkskit.players.OfflineLocalPlayer;

public class MemoryLocalMetrics implements LocalMetrics {

    private final OfflineLocalPlayer holder;
    private int kills, deaths;

    public MemoryLocalMetrics(OfflineLocalPlayer holder) {
        this(holder, 0, 0);
    }

    public MemoryLocalMetrics(OfflineLocalPlayer holder, int kills, int deaths) {
        this.holder = holder;
        this.kills = kills;
        this.deaths = deaths;
    }

    @Override
    public OfflineLocalPlayer getHolder() { return holder; }

    @Override
    public int getKillsCount() { return kills; }

    @Override
    public void incrementKillsCount() { this.kills++; }

    @Override
    public int getDeathsCount() { return deaths; }

    @Override
    public void incrementDeathsCount() { this.deaths++; }

    @Override
    public double getKDR() {
        try {
            return kills / deaths;
        } catch (Exception e) {
            return Double.NaN;
        }
    }

    @Override
    public void updateAll(int kills, int deaths) {
        if(kills < 0 || deaths < 0) {
            this.kills = 0;
            this.deaths = 0;
            return;
        }
        this.kills = kills;
        this.deaths = deaths;
    }
}
