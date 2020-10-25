package me.aiglez.lonkskit.events;

import com.google.common.base.Preconditions;
import me.aiglez.lonkskit.kits.Kit;
import org.bukkit.event.Event;

import javax.annotation.Nonnull;

public abstract class KitEvent extends Event {

    private final Kit kit;

    public KitEvent(Kit kit) {
        super();
        Preconditions.checkNotNull(kit, "kit may not be null");
        this.kit = kit;
    }

    @Nonnull
    public Kit getKit() { return kit; }

}
