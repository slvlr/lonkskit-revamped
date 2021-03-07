package me.aiglez.lonkskit.abilities.itembased.johan;

import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.events.KitSelectEvent;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.config.yaml.YAMLConfigurationLoader;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class WraithAbility extends ItemStackAbility {

    private final Map<UUID, Location> map = new HashMap<>();
    private final List<LocalPlayer> wraithPlayers = new ArrayList<>();

    public WraithAbility(YAMLConfigurationLoader configurationLoader) throws IOException {
        super("wraith", configurationLoader);
    }

    @Override
    public void whenRightClicked(PlayerInteractEvent e) { }

    @Override
    public void whenLeftClicked(PlayerInteractEvent e) { }

    @Override
    public void registerListeners() {
        Events.subscribe(PlayerInteractEvent.class)
                .filter(e -> wraithPlayers.contains(LocalPlayer.get(e.getPlayer())))
                .handler(e -> {
                    LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());
                    if (e.getPlayer().getLocation().getBlock().getRelative(0, -1, 0).getType() != Material.AIR){
                        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                            if (isItemStack(e.getItem())) {
                                Location location = e.getPlayer().getLocation().clone();
                                int distance = 70;
                                Location to = e.getPlayer().getEyeLocation().clone();
                                while (to.distance(location) <= distance) {
                                    to = to.add(e.getPlayer().getLocation().getDirection().multiply(2));
                                    if (this.isValid(to)) {
                                        if (!cooldown.testSilently(localPlayer)) {
                                            localPlayer.msg("&b[LonksKit] &cPlease wait, {0} second(s) left", cooldown.remainingTime(localPlayer, TimeUnit.SECONDS));
                                            return;
                                        }
                                        e.getPlayer().teleport(to.getWorld().getNearbyPlayers(to, 2.0D, 2.0D, 2.0D).stream().findFirst().get());
                                        map.put(e.getPlayer().getUniqueId(), location);
                                        if (e.getPlayer().getLocation().distance(map.get(e.getPlayer().getUniqueId())) > 2) {
                                            if (!cooldown.test(localPlayer)) {
                                                localPlayer.msg("&b[LonksKit] &cPlease wait, {0} second(s) left", cooldown.remainingTime(localPlayer, TimeUnit.SECONDS));
                                                return;
                                            }
                                        }
                                    }
                                }
                                Schedulers.sync().runLater(() -> map.remove(e.getPlayer().getUniqueId()), configuration.getNode("cooldown").getInt(0) * 20L);
                            }
                        }
                        if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
                            if(isItemStack(e.getItem())) {
                                if (map.containsKey(e.getPlayer().getUniqueId())) {
                                    if (!cooldown.testSilently(LocalPlayer.get(e.getPlayer()))) {
                                        e.getPlayer().teleport(map.get(e.getPlayer().getUniqueId()));
                                        map.remove(e.getPlayer().getUniqueId());
                                        applyEffects(LocalPlayer.get(e.getPlayer()));
                                    }
                                }
                            }
                        }
                    }
                });

        Events.subscribe(KitSelectEvent.class)
                .handler(e -> {
                    if (e.getKit().getBackendName().toUpperCase().contains("WRAITH")) {
                        wraithPlayers.add(e.getLocalPlayer());
                    } else wraithPlayers.remove(e.getLocalPlayer());
                });
    }

    private boolean isValid(Location location) {
        return location.getWorld().getNearbyPlayers(location, 2.0D, 2.0D, 2.0D).stream().findFirst().isPresent();
    }

    private boolean isOnEarth(Location location) {
        return location.getBlock().getRelative(0, -1, 0).getType() != Material.AIR;
    }
}
