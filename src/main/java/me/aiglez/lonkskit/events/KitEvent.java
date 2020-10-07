package me.aiglez.lonkskit.events;

import me.aiglez.lonkskit.kits.Kit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class KitEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Kit kit;

    public KitEvent(Kit kit) {
        this.kit = kit;
    }

    public Kit getKit() { return kit; }

}
