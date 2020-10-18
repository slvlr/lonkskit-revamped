package me.aiglez.lonkskit.abilities.itembased;


import me.aiglez.lonkskit.WorldProvider;
import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.events.KitSelectEvent;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.MetadataProvider;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.config.ConfigurationNode;
import me.lucko.helper.metadata.Metadata;
import me.lucko.helper.metadata.SoftValue;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author AigleZ
 * @date 06/10/2020
 */
public class CowboyAbility extends ItemStackAbility {

    public CowboyAbility(ConfigurationNode configuration) {
        super("cowboy", configuration);
    }

    @Override
    public void whenRightClicked(PlayerInteractEvent e) {
        e.setCancelled(true);
    }

    @Override
    public void whenLeftClicked(PlayerInteractEvent e) { }

    @Override
    public void registerListeners() {
        // spawn horse
        Events.subscribe(KitSelectEvent.class)
                .filter(e -> e.getKit().hasAbility(this))
                .handler(e -> {
                    final LocalPlayer localPlayer = e.getLocalPlayer();
                    if(localPlayer.toBukkit().isInsideVehicle()) {
                        return;
                    }

                    final Horse horse = (Horse) WorldProvider.KP_WORLD.spawnEntity(localPlayer.getLocation(), EntityType.HORSE);
                    horse.getInventory().setSaddle(new ItemStack(Material.SADDLE, 1));
                    horse.getInventory().setArmor(new ItemStack(Material.DIAMOND_HORSE_ARMOR, 1));

                    horse.setTamed(true);
                    horse.setOwner(localPlayer.toBukkit());

                    horse.addPassenger(localPlayer.toBukkit());
                    Metadata.provideForEntity(horse).put(MetadataProvider.HORSE_PERSISTENT, SoftValue.of(localPlayer));
                });

        // persistent horse
        Events.subscribe(EntityDamageEvent.class)
                .filter(e -> e.getEntityType() == EntityType.HORSE)
                .filter(e -> Metadata.provideForEntity(e.getEntity()).has(MetadataProvider.HORSE_PERSISTENT))
                .handler(e -> {
                    e.setDamage(0);
                    e.setCancelled(true);
                });

        // block player from dismounting the horse
        Events.subscribe(VehicleExitEvent.class)
                .filter(e -> e.getExited() instanceof Player)
                .filter(e -> {
                    final LocalPlayer localPlayer = LocalPlayer.get((Player) e.getExited());
                    return localPlayer.hasSelectedKit() && localPlayer.getNullableSelectedKit().hasAbility(this);
                })
                .filter(e -> e.getVehicle().getType() == EntityType.HORSE)
                .filter(e -> Metadata.provideForEntity(e.getVehicle()).has(MetadataProvider.HORSE_PERSISTENT))
                .handler(e -> {
                    final LocalPlayer localPlayer = LocalPlayer.get((Player) e.getExited());
                    if(localPlayer.metadata().has(MetadataProvider.LEAVE_HORSE)) {
                        return;
                    }

                    localPlayer.msg("&6(Cowboy) &cYou can't dismount your horse! (Exit Event)");

                    // we remount him back, because of spigot's 1.16+ issue with mounting
                    Schedulers.sync()
                            .runLater(() -> e.getVehicle().addPassenger(e.getExited()), 2L);
                });

        // kill player's horse when he dies.
        Events.subscribe(EntityDeathEvent.class)
                .filter(AbilityPredicates.possiblyHasAbility(this))
                .handler(e -> {
                    final LocalPlayer localPlayer = LocalPlayer.get((Player) e.getEntity());

                    localPlayer.metadata().put(MetadataProvider.LEAVE_HORSE, SoftValue.of(true));
                    localPlayer.toBukkit().leaveVehicle();

                    WorldProvider.KP_WORLD.getNearbyEntitiesByType(Horse.class, localPlayer.getLocation(), 5, 5, 5)
                            .forEach(horse -> {
                                if(Metadata.provideForEntity(horse).has(MetadataProvider.HORSE_PERSISTENT)) {
                                    final LocalPlayer owner = Metadata.provideForEntity(horse).getOrNull(MetadataProvider.HORSE_PERSISTENT);
                                    if(localPlayer.getUniqueId().equals(owner.getUniqueId())) {
                                        horse.damage(999);
                                        horse.remove();
                                        localPlayer.msg("&6(Cowboy) &cSince you died, your horse died too.");
                                    }
                                }
                            });
                });
    }
}
