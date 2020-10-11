package me.aiglez.lonkskit.events;

import me.aiglez.lonkskit.kits.Kit;
import me.aiglez.lonkskit.players.LocalPlayer;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public class KitSelectEvent extends KitEvent {

    private static final HandlerList HANDLERS = new HandlerList();
    private final LocalPlayer localPlayer;

    public KitSelectEvent(@Nonnull Kit kit, @Nonnull LocalPlayer localPlayer) {
        super(kit);
        this.localPlayer = localPlayer;
    }

    @Nonnull
    public LocalPlayer getLocalPlayer() {
        return localPlayer;
    }

    @Override
    public HandlerList getHandlers() { return HANDLERS; }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
