package me.aiglez.lonkskit.listeners

import me.aiglez.lonkskit.KitPlugin
import me.aiglez.lonkskit.players.LocalPlayer
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

class CombatListeners(plugin: KitPlugin) : Listener {

    init {
        plugin.registerListener(this)
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onHitEvent(e: EntityDamageByEntityEvent) {
        if (e.entity !is Player) return
        val victim = LocalPlayer.get(e.entity as Player)
        var damager : LocalPlayer? = null

        if (e.damager is Player ) damager = LocalPlayer.get(e.damager as Player)
        else if (e.damager is Projectile) damager = LocalPlayer.get((e.damager as Projectile).shooter as Player?)
        if (!victim.isValid || !damager?.isValid!!) return

        victim.setLastAttacker(damager)
        damager.setLastAttacker(damager)

        victim.msg("&b[DEBUG] &cYou have entered in combat with {0}.", damager.lastKnownName)
        damager.msg("&b[DEBUG] &cYou have entered in combat with {0}.", victim.lastKnownName)
    }
    fun Player.msg(message: String) {
        this.sendMessage(ChatColor.translateAlternateColorCodes('&',message))
    }
}