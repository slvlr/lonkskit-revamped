package me.aiglez.lonkskit.abilities.itembased

import me.lucko.helper.config.yaml.YAMLConfigurationLoader
import me.aiglez.lonkskit.abilities.ItemStackAbility
import me.aiglez.lonkskit.abilities.helpers.ConstructHelper
import me.aiglez.lonkskit.players.LocalPlayer
import org.bukkit.event.player.PlayerInteractEvent

class DomeBuilderAbility(configurationLoader: YAMLConfigurationLoader?) :
    ItemStackAbility("domebuilder", configurationLoader) {
    override fun whenRightClicked(e: PlayerInteractEvent) {
        e.isCancelled = true
        val localPlayer = LocalPlayer.get(e.player)
        val result = ConstructHelper.buildDome(localPlayer.location, 300)
        if (result) {
            localPlayer.msg("&a(DomeBuilder - Debug) &aSuccessfully built a dome!")
        } else {
            localPlayer.msg("&a(DomeBuilder - Debug) &cYou can't build a dome there!")
        }
    }

    override fun whenLeftClicked(e: PlayerInteractEvent) {
        e.isCancelled = true
    }

    override fun registerListeners() {}
}