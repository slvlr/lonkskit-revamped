package me.aiglez.lonkskit.players;

import me.aiglez.lonkskit.kits.Kit;
import me.aiglez.lonkskit.players.impl.MemoryLocalRent;

@SuppressWarnings({"unused", "RedundantSuppression"})
public interface LocalRent {

    Kit getRented();

    LocalPlayer getLocalPlayer();

    int getUses();

    void incrementUses();

    boolean isValid();

    default int getLeftUses() {
        if(getRented() == null) return 0;
        return getRented().getUsesPerRent() - getUses();
    }

    static LocalRent of(LocalPlayer localPlayer, Kit kit) { return new MemoryLocalRent(localPlayer, kit); }

    static LocalRent of(LocalPlayer localPlayer, Kit kit, int uses) { return new MemoryLocalRent(localPlayer, kit, uses); }
}
