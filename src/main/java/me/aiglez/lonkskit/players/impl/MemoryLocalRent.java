package me.aiglez.lonkskit.players.impl;

import me.aiglez.lonkskit.kits.Kit;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.players.LocalRent;

public class MemoryLocalRent implements LocalRent {

    private final LocalPlayer localPlayer;
    private final Kit rentedKit;

    private int uses;

    public MemoryLocalRent(LocalPlayer localPlayer, Kit rentedKit) { this(localPlayer, rentedKit, 0); }

    public MemoryLocalRent(LocalPlayer localPlayer, Kit rentedKit, int uses) {
        this.localPlayer = localPlayer;
        this.rentedKit = rentedKit;
        this.uses = uses;
    }

    @Override
    public Kit getRented() { return rentedKit; }

    @Override
    public LocalPlayer getLocalPlayer() { return localPlayer; }

    @Override
    public int getUses() { return uses; }

    @Override
    public void incrementUses() { this.uses++; }

    @Override
    public boolean isValid() { return rentedKit != null && uses < rentedKit.getUsesPerRent(); }
}
