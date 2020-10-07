package me.aiglez.lonkskit.events;

import me.aiglez.lonkskit.kits.Kit;
import org.bukkit.event.Event;

public abstract class KitEvent extends Event {

    private final Kit kit;

    public KitEvent(Kit kit) {
        this.kit = kit;
    }

    public Kit getKit() { return kit; }

}
