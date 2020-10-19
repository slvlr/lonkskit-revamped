package me.aiglez.lonkskit.abilities.external.itembased;

import me.aiglez.lonkskit.KitPlugin;
import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.events.KitSelectEvent;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.items.ItemStackBuilder;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.config.ConfigurationNode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class WraithAbility extends ItemStackAbility {
    private ItemStack item;
    private Map<UUID, Location> map = new HashMap<UUID, Location>();
    private final List<LocalPlayer> wraithPlayers = new ArrayList<>();

    public WraithAbility(ConfigurationNode configuration) {
        super("wraith", configuration);
        item = ItemStackBuilder.of(Material.BLACK_DYE).build();
    }

    ///*/*/*/*/*/*/*/*/*//
    @Override
    public ItemStack getItemStack() {
        return this.item;
    }

    @Override
    public boolean isItemStack(ItemStack item) {
        return this.item.isSimilar(item);
    }

    @Override
    public void whenRightClicked(PlayerInteractEvent e) {

    }

    @Override
    public void whenLeftClicked(PlayerInteractEvent e) {

    }

    @Override
    public void handleListeners() {
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
                            Schedulers.sync().runLater(() -> map.remove(e.getPlayer().getUniqueId()), (long) (configuration.getNode("cooldown").getInt(0) * 20));
                        }
                    }
                        if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
                            if (map.containsKey(e.getPlayer().getUniqueId())) {
                                if (!cooldown.testSilently(LocalPlayer.get(e.getPlayer()))) {
                                    e.getPlayer().teleport(map.get(e.getPlayer().getUniqueId()));
                                    map.remove(e.getPlayer().getUniqueId());
                                    e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 400, 3)); //Customizable
                                }
                            }
                        }
                    }
                });

        Events.subscribe(KitSelectEvent.class)
                .filter(e -> e.getKit() != null)
                .handler(e -> {
                    if (e.getKit().getBackendName().toUpperCase().contains("WRAITH")) {
                        wraithPlayers.add(e.getLocalPlayer());
                    } else if (wraithPlayers.contains(e.getLocalPlayer()))
                        wraithPlayers.remove(e.getLocalPlayer());

                });/*
                .filter(e -> e.getKit().getBackendName().toUpperCase().contains("WRAITH"))
                .handler(e -> wraithPlayers.add(e.getLocalPlayer()));
*/

    }

    private boolean isValid(Location location) {
        return location.getWorld().getNearbyPlayers(location, 2.0D, 2.0D, 2.0D).stream().findFirst().isPresent();
    }

    private boolean isOnEarth(Location location) {
        return location.getBlock().getRelative(0, -1, 0).getType() != Material.AIR;
    }
}
/*while(to.distance(e.getPlayer().getEyeLocation()) <= (double)range) {
                        from = to;
                        to = to.add(e.getPlayer().getLocation().getDirection().normalize().multiply(1));
                        if (this.isValid(to.getBlock().getType())) {
                            if (from.getWorld().getNearbyPlayers(from, 20.0D, 20.0D, 20.0D).stream().findFirst().isPresent()) {
                                e.getPlayer().teleport(from.getWorld().getNearbyPlayers(from, 20.0D, 20.0D, 20.0D).stream().findFirst().get());
                                e.getPlayer().sendMessage("Teleported ...");
                                return;
                            }
                        }
                    }*/