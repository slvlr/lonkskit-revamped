package me.aiglez.lonkskit.listeners

import me.aiglez.lonkskit.KitPlugin
import me.aiglez.lonkskit.players.LocalPlayer
import me.aiglez.lonkskit.utils.Various.isThrowable
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.PlayerInventory

class DisconnectListener(plugin: KitPlugin) : Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    fun onQuitEvent(e: PlayerQuitEvent) {
        val localPlayer = LocalPlayer.get(e.player)
        if (localPlayer.isValid){
            localPlayer.toBukkit().health = 0.0
            if (localPlayer.hasSelectedKit()){
                localPlayer.setSelectedKit(null)
                localPlayer.setInKP(true)
                if (!localPlayer.inventory.containsThrowable()){
                    localPlayer.inventory.clear()
                }
            }
        }
    }

    init {
        plugin.registerListener(this)
    }

    private fun PlayerInventory.containsThrowable() : Boolean{
        return this.contents.any(::isThrowable)
    }
}