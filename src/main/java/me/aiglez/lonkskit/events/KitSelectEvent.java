package me.aiglez.lonkskit.events;

import me.aiglez.lonkskit.kits.Kit;
import me.aiglez.lonkskit.players.LocalPlayer;
import org.bukkit.event.HandlerList;

public class KitSelectEvent extends KitEvent {

    private static final HandlerList HANDLERS = new HandlerList();
    private final LocalPlayer localPlayer;

    public KitSelectEvent(Kit kit, LocalPlayer localPlayer) {
        super(kit);
        this.localPlayer = localPlayer;
    }

    public LocalPlayer getLocalPlayer() {
        return localPlayer;
    }

    @Override
    public HandlerList getHandlers() { return HANDLERS; }
}
