package me.aiglez.lonkskit.data;

import me.aiglez.lonkskit.players.LocalMetrics;
import me.aiglez.lonkskit.players.LocalPlayer;

public class MemoryMetrics implements LocalMetrics {

    private final LocalPlayer holder;
    private int kills, deaths;

    public MemoryMetrics(LocalPlayer holder) {
        this(holder, 0, 0);
    }

    public MemoryMetrics(LocalPlayer holder, int kills, int deaths) {
        this.holder = holder;
        this.kills = kills;
        this.deaths = deaths;
    }

    @Override
    public LocalPlayer getHolder() { return holder; }

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
