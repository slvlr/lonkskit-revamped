package me.aiglez.lonkskit.players;

import me.aiglez.lonkskit.kits.Kit;
import me.aiglez.lonkskit.players.impl.MemoryLocalRent;

@SuppressWarnings({"unused", "RedundantSuppression"})
public interface LocalRent {

    Kit getRented();

    OfflineLocalPlayer getOfflineLocalPlayer();

    int getUses();

    void incrementUses();

    void decrementUses();

    boolean isValid();

    default int getLeftUses() {
        if(getRented() == null) return 0;
        return getRented().getUsesPerRent() - getUses();
    }

    static LocalRent of(OfflineLocalPlayer offlineLocalPlayer, Kit kit) { return new MemoryLocalRent(offlineLocalPlayer, kit); }

    static LocalRent of(OfflineLocalPlayer offlineLocalPlayer, Kit kit, int uses) { return new MemoryLocalRent(offlineLocalPlayer, kit, uses); }
}
