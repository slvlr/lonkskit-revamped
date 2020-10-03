package me.aiglez.lonkskit.players;

import me.aiglez.lonkskit.data.MemoryRent;
import me.aiglez.lonkskit.kits.Kit;

@SuppressWarnings("unused")
public interface LocalRent {

    Kit getRented();

    LocalPlayer getLocalPlayer();

    int getUses();

    default int getLeftUses() {
        if(getRented() == null) return 0;
        return getRented().getUsesPerRent() - getUses();
    }

    void incrementUses();

    boolean isValid();

    static LocalRent of(LocalPlayer localPlayer, Kit kit) { return new MemoryRent(localPlayer, kit); }

    static LocalRent of(LocalPlayer localPlayer, Kit kit, int uses) { return new MemoryRent(localPlayer, kit, uses); }
}
