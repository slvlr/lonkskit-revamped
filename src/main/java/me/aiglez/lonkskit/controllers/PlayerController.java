package me.aiglez.lonkskit.controllers;

import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;
import me.aiglez.lonkskit.Constants;
import me.aiglez.lonkskit.KitPlugin;
import me.aiglez.lonkskit.kits.Kit;
import me.aiglez.lonkskit.messages.Messages;
import me.aiglez.lonkskit.messages.Replaceable;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.struct.HotbarItemStack;
import me.aiglez.lonkskit.utils.Logger;
import me.aiglez.lonkskit.utils.Various;
import me.aiglez.lonkskit.utils.items.ItemStackBuilder;
import me.lucko.helper.config.ConfigurationNode;
import me.lucko.helper.config.objectmapping.ObjectMappingException;
import org.bukkit.Material;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class PlayerController {

    private final Set<HotbarItemStack> loggingItems;

    public PlayerController() {
        this.loggingItems = Sets.newHashSet();
    }

    public void loadHotbarItems() {
        final ConfigurationNode config = KitPlugin.getSingleton().getConf().getNode("hotbar");

        for (final Object childName : config.getChildrenMap().keySet()) {
            final ConfigurationNode child = config.getNode(childName);

            final String name = child.getNode("name").getString("");
            final Material material = Material.matchMaterial(child.getNode("material").getString("STONE"));

            try {
                final List<String> lore = child.getNode("lore").getList(new TypeToken<String>() {});
                final List<String> playerCommands = child.getNode("lore").getList(new TypeToken<String>() {});
                final List<String> consoleCommands = child.getNode("lore").getList(new TypeToken<String>() {});

                final HotbarItemStack hotbarItem = new HotbarItemStack(
                        ItemStackBuilder.of(material).name(name).lore(lore).withNBT("logging-item").build(),
                        playerCommands,
                        consoleCommands
                );

                loggingItems.add(hotbarItem);

            } catch (NullPointerException | ObjectMappingException e) {
                Logger.severe("Couldn't load a hotbar itemstack, skipping this one.");
                e.printStackTrace();
            }
        }
    }

    public Set<HotbarItemStack> getHotbarItems() {
        return Collections.unmodifiableSet(this.loggingItems);
    }

    public void handleDeathOf(LocalPlayer killer, LocalPlayer victim) {
        final Kit using = killer.getNullableSelectedKit();
        killer.incrementPoints(Constants.POINTS_PER_KILL);

        killer.getMetrics().incrementKillsCount();
        if(killer.getMetrics().hasKillStreak()) {
            Various.broadcast(Replaceable.handle(
                    Messages.PLAYER_METRICS_KILLSTREAK.getValue(), killer.getLastKnownName(), killer.getMetrics().getKillStreak()
            ));
        }

        victim.getMetrics().incrementDeathsCount();
        if(victim.getMetrics().hasKillStreak()) {
            Various.broadcast(Replaceable.handle(
                    Messages.PLAYER_METRICS_RUINED_KILLSTREAK.getValue(), killer.getLastKnownName(), victim.getLastKnownName(), killer.getMetrics().getKillStreak()
            ));
            victim.getMetrics().resetKillStreak();
        }

        killer.setLastAttacker(null);
        victim.setLastAttacker(null);

        killer.msg("&b[LonksKit] &aYou have been awarded {0} points for killing {1}.",
                Constants.POINTS_PER_KILL, victim.getLastKnownName()
        );

        victim.msg("&b[LonksKit] &cYou have been killed by {0}.",
                killer.getLastKnownName()
        );

        Various.broadcast("&7{0} &fwas killed by &c{1} &fusing &b{2}&f.",
                victim.getLastKnownName(), killer.getLastKnownName(), using.getDisplayName()
        );
    }
}
