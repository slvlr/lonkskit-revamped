package me.aiglez.lonkskit.players.impl;

import me.aiglez.lonkskit.kits.Kit;
import me.aiglez.lonkskit.players.LocalRent;
import me.aiglez.lonkskit.players.OfflineLocalPlayer;

public class MemoryLocalRent implements LocalRent {

    private final OfflineLocalPlayer renter;
    private final Kit rentedKit;

    private int uses;


    public MemoryLocalRent(OfflineLocalPlayer renter, Kit rentedKit) { this(renter, rentedKit, 0); }

    public MemoryLocalRent(OfflineLocalPlayer renter, Kit rentedKit, int uses) {
        this.renter = renter;
        this.rentedKit = rentedKit;
        this.uses = uses;
    }


    @Override
    public Kit getRented() { return this.rentedKit; }

    @Override
    public OfflineLocalPlayer getOfflineLocalPlayer() { return this.renter; }

    @Override
    public int getUses() { return this.uses; }

    @Override
    public void incrementUses() { this.uses++; }

    @Override
    public boolean isValid() { return this.rentedKit != null && this.uses < this.rentedKit.getUsesPerRent(); }
}
