package me.aiglez.lonkskit.abilities.functional;

import me.aiglez.lonkskit.WorldProvider;
import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.FunctionalAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.lucko.helper.Events;
import me.lucko.helper.config.yaml.YAMLConfigurationLoader;
import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import java.io.IOException;

/**
 * @author AigleZ
 * @date 11/10/2020
 */
public class    HotheadAbility extends FunctionalAbility {

    public HotheadAbility(YAMLConfigurationLoader configurationLoader) throws IOException {
        super("hothead", configurationLoader);
    }

    @Override
    public void registerListeners() {
        Events.subscribe(PlayerToggleSneakEvent.class)
                .filter(AbilityPredicates.hasAbility(this))
                .handler(e -> {
                    final LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());
                    applyEffects(localPlayer);

                    WorldProvider.KP_WORLD.getNearbyPlayers(localPlayer.getLocation(),
                            configuration.getNode("radius", "x-axis").getDouble(1D),
                            configuration.getNode("radius", "y-axis").getDouble(1D),
                            configuration.getNode("radius", "z-axis").getDouble(1D))
                            .forEach(player -> {
                                if(!localPlayer.getUniqueId().equals(player.getUniqueId())) {
                                    player.setFireTicks(40);
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&b[DEBUG] &cYou have entered in combat with " + localPlayer.getLastKnownName()));
                                    localPlayer.msg("&b[DEBUG] &cYou have entered in combat with {0}.",player.getDisplayName());
                                }
                            });
                });

    }
}
