package me.aiglez.lonkskit.events;

import me.aiglez.lonkskit.kits.Kit;
import org.bukkit.event.Event;

import javax.annotation.Nonnull;

public abstract class KitEvent extends Event {

    private final Kit kit;

    public KitEvent(@Nonnull Kit kit) {
        super();
        this.kit = kit;
    }

    @Nonnull
    public Kit getKit() { return kit; }

}
