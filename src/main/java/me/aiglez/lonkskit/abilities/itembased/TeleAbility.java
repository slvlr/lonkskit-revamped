package me.aiglez.lonkskit.abilities.itembased;

import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.lucko.helper.Events;
import me.lucko.helper.config.yaml.YAMLConfigurationLoader;
import org.bukkit.Material;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class TeleAbility extends ItemStackAbility {

    public TeleAbility(YAMLConfigurationLoader configurationLoader) throws IOException {
        super("tele", configurationLoader);
    }

    @Override
    public void registerListeners() {
        Events.subscribe(BlockPlaceEvent.class)
                .filter(e -> e.getBlock().getType() == Material.END_PORTAL_FRAME)
                .filter(e -> LocalPlayer.get(e.getPlayer()).hasSelectedKit())
                .filter(e -> LocalPlayer.get(e.getPlayer()).getNullableSelectedKit().getAbilities().contains(this))
                .handler(e -> {
                    if (!cooldown.test(LocalPlayer.get(e.getPlayer()))) {
                        LocalPlayer.get(e.getPlayer()).msg("&b[LonksKit] &cPlease wait, {0} second(s) left", cooldown.remainingTime(LocalPlayer.get(e.getPlayer()), TimeUnit.SECONDS));
                        e.setCancelled(true);
                    }else {
                        double x = getConfiguration().getNode("radius", "x-axis").getInt();
                        double y = getConfiguration().getNode("radius", "y-axis").getInt();
                        double z = getConfiguration().getNode("radius", "z-axis").getInt();
                        e.getPlayer().getWorld().getNearbyPlayers(e.getBlock().getLocation(), x, y, z, player -> {
                            if (!LocalPlayer.get(player).hasSelectedKit()) {
                                player.teleport(e.getBlock().getLocation().clone().add(0.5, 1, 0.5));
                            } else if (!LocalPlayer.get(player).getNullableSelectedKit().getBackendName().equalsIgnoreCase(LocalPlayer.get(e.getPlayer()).getNullableSelectedKit().getBackendName())) {
                                player.teleport(e.getBlock().getLocation().clone().add(0.5, 1, 0.5));
                            }
                            return true;
                        });
                    }
                });
    }

    @Override
    public void whenRightClicked(PlayerInteractEvent e) {
        if (e.hasBlock()) {
            if (Objects.requireNonNull(e.getClickedBlock()).getType() == Material.END_PORTAL_FRAME) {
                if(!cooldown.test(LocalPlayer.get(e.getPlayer()))){
                    LocalPlayer.get(e.getPlayer()).msg("&b[LonksKit] &cPlease wait, {0} second(s) left", cooldown.remainingTime(LocalPlayer.get(e.getPlayer()), TimeUnit.SECONDS));
                    e.setCancelled(true);
                    return;
                }
                double x = getConfiguration().getNode("radius","x-axis").getInt();
                double y = getConfiguration().getNode("radius","y-axis").getInt();
                double z = getConfiguration().getNode("radius","z-axis").getInt();
                e.getPlayer().getWorld().getNearbyPlayers(e.getClickedBlock().getLocation(), x, y, z)
                        .forEach(player -> {
                            if (!LocalPlayer.get(player).hasSelectedKit()) {
                                player.teleport(e.getClickedBlock().getLocation().clone().add(0.5D,1,0.5D));
                            } else if (!LocalPlayer.get(player).getNullableSelectedKit().getBackendName().equalsIgnoreCase(LocalPlayer.get(e.getPlayer()).getNullableSelectedKit().getBackendName())) {
                                player.teleport(e.getClickedBlock().getLocation().clone().add(0.5D,1,0.5D));
                            }
                        });


            }
        }
    }

    @Override
    public void whenLeftClicked(PlayerInteractEvent e) {

    }

}
