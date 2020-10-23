package me.aiglez.lonkskit.listeners;

import me.aiglez.lonkskit.KitPlugin;
import org.bukkit.event.Listener;

public class InteractListeners implements Listener {

    public InteractListeners(KitPlugin plugin) {
        plugin.registerListener(this);
    }

}
