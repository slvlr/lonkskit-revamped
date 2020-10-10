package me.aiglez.lonkskit.abilities.itembased;


import me.aiglez.lonkskit.WorldProvider;
import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.MetadataProvider;
import me.aiglez.lonkskit.utils.items.ItemStackBuilder;
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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author AigleZ
 * @date 06/10/2020
 */
public class CowboyAbility extends ItemStackAbility {

    private final ItemStack item;

    public CowboyAbility(ConfigurationNode configuration) {
        super("cowboy", configuration);
        this.item = ItemStackBuilder.of(Material.SADDLE)
                .name("&eCowboy")
                .build();
    }

    @Override
    public ItemStack getItemStack() { return this.item; }

    @Override
    public boolean isItemStack(ItemStack item) { return this.item.isSimilar(item); }

    @Override
    public void whenRightClicked(PlayerInteractEvent e) {
        e.setCancelled(true);
        final LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());
        if(localPlayer.toBukkit().isInsideVehicle()) {
            localPlayer.msg("&6(Cowboy) &cYou are already inside a vehicle!");
            return;
        }

        final Horse horse = (Horse) WorldProvider.KP_WORLD.spawnEntity(localPlayer.getLocation(), EntityType.HORSE);
        horse.getInventory().setSaddle(new ItemStack(Material.SADDLE, 1));
        horse.getInventory().setArmor(new ItemStack(Material.DIAMOND_HORSE_ARMOR, 1));

        horse.setTamed(true);
        horse.setOwner(localPlayer.toBukkit());

        horse.addPassenger(localPlayer.toBukkit());
        Metadata.provideForEntity(horse).put(MetadataProvider.HORSE_PERSISTENT, SoftValue.of(true));
    }

    @Override
    public void whenLeftClicked(PlayerInteractEvent e) { }

    @Override
    public void handleListeners() {
        // persistent horse
        Events.subscribe(EntityDamageEvent.class)
                .filter(e -> e.getEntityType() == EntityType.HORSE)
                .filter(e -> Metadata.provideForEntity(e.getEntity()).has(MetadataProvider.HORSE_PERSISTENT))
                .handler(e -> {
                    e.setDamage(0);
                    e.setCancelled(true);
                });

        // block player from dismounting an entity
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
                    localPlayer.msg("&6(Cowboy) &cYou can't dismount your horse! (Exit Event) &c(cancellable: {0})", e.isCancellable());

                    Schedulers.sync()
                            .runLater(() -> {
                                e.getVehicle().addPassenger(e.getExited());
                            }, 2L);
                });

    }
}
