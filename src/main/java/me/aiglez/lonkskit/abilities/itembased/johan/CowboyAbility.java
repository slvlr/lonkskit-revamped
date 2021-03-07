package me.aiglez.lonkskit.abilities.itembased.johan;


import me.aiglez.lonkskit.WorldProvider;
import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.events.KitSelectEvent;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.MetadataProvider;
import me.lucko.helper.Events;
import me.lucko.helper.config.yaml.YAMLConfigurationLoader;
import me.lucko.helper.metadata.Metadata;
import me.lucko.helper.metadata.SoftValue;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author JOHAN
 * @date 06/10/2020
 */
public class CowboyAbility extends ItemStackAbility {
    public static final Map<LocalPlayer,Horse> cowboys = new HashMap<>();

    public CowboyAbility(YAMLConfigurationLoader configurationLoader) throws IOException {
        super("cowboy", configurationLoader);
    }

    @Override
    public void registerListeners() {
        Events.subscribe(KitSelectEvent.class)
                .filter(e -> e.getKit().hasAbility(this))
                .handler(e -> {
                    LocalPlayer localPlayer = e.getLocalPlayer();
                    createHorse(localPlayer);
                });
        Events.subscribe(VehicleExitEvent.class)
                .filter(e -> e.getVehicle() instanceof Horse)
                .filter(e -> Metadata.provideForEntity(e.getVehicle()).has(MetadataProvider.HORSE_PERSISTENT))
                .handler(e -> {
                    e.getVehicle().remove();
                    cowboys.remove(LocalPlayer.get((Player) e.getExited()));
                });
        Events.subscribe(PlayerDeathEvent.class)
                .filter(e -> cowboys.containsKey(LocalPlayer.get(e.getEntity())))
                .handler(e -> cowboys.entrySet().stream().filter(a -> a.getKey() == LocalPlayer.get(e.getEntity())).findAny().ifPresent(x -> {
                    x.getValue().remove();
                    cowboys.remove(x.getKey());
                }));
        Events.subscribe(EntityDropItemEvent.class)
                .filter(e -> e.getItemDrop().getItemStack() != null)
                .filter(e -> e.getItemDrop().getItemStack().getType() == Material.LEAD)
                .handler(e -> e.setCancelled(true));
        Events.subscribe(PlayerQuitEvent.class, EventPriority.HIGHEST)
                .filter(e -> cowboys.containsKey(LocalPlayer.get(e.getPlayer())))
                .handler(e -> cowboys.entrySet().stream().filter(a -> a.getKey() == LocalPlayer.get(e.getPlayer())).findAny().ifPresent(x -> {
                    cowboys.get(LocalPlayer.get(e.getPlayer())).getInventory().clear();
                    cowboys.get(LocalPlayer.get(e.getPlayer())).setHealth(0);
                }));
        Events.subscribe(EntityDeathEvent.class)
                .filter(e -> e.getEntity() instanceof Horse)
                .handler(e -> {
                    e.getDrops().clear();
                    e.setDroppedExp(0);
                });
    }

    @Override
    public void whenRightClicked(PlayerInteractEvent e) {
        LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());
        if (!cooldown.test(localPlayer)) {
            localPlayer.msg("&b[LonksKit] &cPlease wait, {0} second(s) left", cooldown.remainingTime(localPlayer, TimeUnit.SECONDS));
            return;
        }
        createHorse(LocalPlayer.get(e.getPlayer()));
    }

    @Override
    public void whenLeftClicked(PlayerInteractEvent e) {

    }
    public void createHorse(LocalPlayer localPlayer){
        if (cowboys.containsKey(localPlayer)){
            cowboys.get(localPlayer).getInventory().clear();
            cowboys.get(localPlayer).setHealth(0);
        }
        final Horse horse = (Horse) WorldProvider.KP_WORLD.spawnEntity(localPlayer.getLocation(), EntityType.HORSE);
        horse.getInventory().setSaddle(new ItemStack(Material.SADDLE, 1));
        horse.getInventory().setArmor(new ItemStack(Material.DIAMOND_HORSE_ARMOR, 1));
        horse.addPassenger(localPlayer.toBukkit());
        horse.setTamed(true);
        horse.setOwner(localPlayer.toBukkit());
        cowboys.put(localPlayer,horse);
        horse.addPassenger(localPlayer.toBukkit());
        Metadata.provideForEntity(horse).put(MetadataProvider.HORSE_PERSISTENT, SoftValue.of(localPlayer));
    }
}
