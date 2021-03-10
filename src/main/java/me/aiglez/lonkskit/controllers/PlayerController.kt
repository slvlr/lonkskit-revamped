package me.aiglez.lonkskit.controllers

import com.google.common.collect.Sets
import com.google.common.reflect.TypeToken
import me.aiglez.lonkskit.Constants
import me.aiglez.lonkskit.struct.HotbarItemStack
import me.aiglez.lonkskit.KitPlugin
import me.aiglez.lonkskit.listeners.FeaturesListeners
import me.aiglez.lonkskit.messages.Messages
import me.aiglez.lonkskit.messages.Replaceable
import me.aiglez.lonkskit.players.LocalPlayer
import me.aiglez.lonkskit.utils.Logger
import java.lang.NullPointerException
import me.lucko.helper.config.objectmapping.ObjectMappingException
import java.util.stream.Collectors
import me.aiglez.lonkskit.utils.Various
import me.aiglez.lonkskit.utils.items.ItemStackBuilder
import me.lucko.helper.Services
import org.bukkit.Material
import kotlin.math.min

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "NAME_SHADOWING")
class PlayerController {
    private val loggingItems: MutableSet<HotbarItemStack>
    fun loadHotbarItems() {
        val config = Services.load(KitPlugin::class.java).conf.getNode("hotbar")
        for (childName in config.childrenMap.keys) {
            val child = config.getNode(childName!!)
            val name = child.getNode("name").getString("")
            val material : Material? = Material.matchMaterial(child.getNode("material").getString("STONE"))
            val order = child.getNode("order").int
            try {
                val lore = child.getNode("lore").getList(object : TypeToken<String?>() {})
                val playerCommands = child.getNode("lore").getList(object : TypeToken<String?>() {})
                val consoleCommands = child.getNode("lore").getList(object : TypeToken<String?>() {})
                val hotbarItem = HotbarItemStack(
                    ItemStackBuilder.of(material).name(name).lore(lore).withNBT("logging-item").build(),
                    playerCommands,
                    consoleCommands,
                    name, order
                )
                loggingItems.add(hotbarItem)
            } catch (e: NullPointerException) {
                Logger.severe("Couldn't load a hotbar itemstack, skipping this one.")
                e.printStackTrace()
            } catch (e: ObjectMappingException) {
                Logger.severe("Couldn't load a hotbar itemstack, skipping this one.")
                e.printStackTrace()
            }
        }
    }

    val hotbarItems: Set<HotbarItemStack>
        get() = loggingItems.stream().sorted { hotbarItemStack: HotbarItemStack, t1: HotbarItemStack ->
            min(
                t1.order,
                hotbarItemStack.order
            )
        }
            .collect(Collectors.toCollection { LinkedHashSet() })

    fun handleDeathOf(killer: LocalPlayer, victim: LocalPlayer) {
        val using = killer.nullableSelectedKit
        killer.incrementPoints(Constants.POINTS_PER_KILL)
        killer.metrics.incrementKillsCount()
        if (killer.metrics.hasKillStreak()) {
            Various.broadcast(
                Replaceable.handle(
                    Messages.PLAYER_METRICS_KILLSTREAK.value, killer.lastKnownName, killer.metrics.killStreak
                )
            )
        }
        victim.metrics.incrementDeathsCount()
        if (victim.metrics.hasKillStreak()) {
            Various.broadcast(
                Replaceable.handle(
                    Messages.PLAYER_METRICS_RUINED_KILLSTREAK.value,
                    killer.lastKnownName,
                    victim.lastKnownName,
                    killer.metrics.killStreak
                )
            )
            victim.metrics.resetKillStreak()
        }
        killer.setLastAttacker(null)
        victim.setLastAttacker(null)
        killer.msg(
            "&b[LonksKit] &aYou have been awarded {0} points for killing {1}.",
            Constants.POINTS_PER_KILL, victim.lastKnownName
        )
        victim.msg(
            "&b[LonksKit] &cYou have been killed by {0}.",
            killer.lastKnownName
        )
        if (using != null) {
            Various.broadcast(
                "&7{0} &fwas killed by &c{1} &fusing &b{2}&f.",
                victim.lastKnownName, killer.lastKnownName, using.displayName
            )
        } else {
            Various.broadcast(
                "&7{0} &fwas killed by &c{1}&f.",
                victim.lastKnownName, killer.lastKnownName
            )
        }
    }

    fun handleSuicide(victim: LocalPlayer) {
        victim.msg("&6[&bLonksKits&6]&c You have died!")
        victim.setInKP(true)
        victim.lastAttacker.ifPresent { victim -> victim.setLastAttacker(null) }
        FeaturesListeners.killstreaks.replace(victim,0)
    }

    init {
        loggingItems = Sets.newHashSet()
    }
}