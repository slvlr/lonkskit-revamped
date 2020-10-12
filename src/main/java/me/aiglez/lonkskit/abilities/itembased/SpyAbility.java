package me.aiglez.lonkskit.abilities.itembased;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import me.aiglez.lonkskit.KitPlugin;
import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.MetadataProvider;
import me.aiglez.lonkskit.utils.items.ItemStackBuilder;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.config.ConfigurationNode;
import me.lucko.helper.event.filter.EventFilters;
import me.lucko.helper.metadata.SoftValue;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author AigleZ
 * @date 10/10/2020
 */
public class SpyAbility extends ItemStackAbility {

    private final ItemStack item;

    public SpyAbility(ConfigurationNode configuration) {
        super("spy", configuration);
        this.item = ItemStackBuilder.of(Material.BOW)
                .name("&eSpy")
                .build();
    }

    @Override
    public ItemStack getItemStack() { return this.item; }

    @Override
    public boolean isItemStack(ItemStack item) { return this.item.isSimilar(item); }

    @Override
    public void whenRightClicked(PlayerInteractEvent e) {

    }

    @Override
    public void whenLeftClicked(PlayerInteractEvent e) {

    }

    @Override
    public void handleListeners() {
        Events.subscribe(EntityShootBowEvent.class)
                .filter(AbilityPredicates.humanHasAbility(this))
                .filter(e -> isItemStack(e.getBow()))
                .filter(e -> e.getProjectile() instanceof Arrow)
                .handler(e -> {
                    final LocalPlayer localPlayer = LocalPlayer.get((Player) e.getEntity());
                    final Arrow arrow = (Arrow) e.getProjectile();

                    // spectator - start
                    arrow.remove();

                    localPlayer.metadata().put(MetadataProvider.SPY_PLAYER, SoftValue.of(true));
                    localPlayer.toBukkit().setGameMode(GameMode.SPECTATOR);
                    localPlayer.toBukkit().setVelocity(
                            localPlayer.getLocation().getDirection().multiply(5.0 * e.getForce()));

                    localPlayer.toBukkit().setNoDamageTicks(10); // no damage

                    final AtomicInteger passed = new AtomicInteger(0);
                    Schedulers.sync()
                            .runRepeating(t -> {
                                if(localPlayer.toBukkit().isOnGround() && passed.intValue() > 20) {
                                    localPlayer.toBukkit().setNoDamageTicks(0);
                                    localPlayer.toBukkit().setFallDistance(0);

                                    localPlayer.metadata().remove(MetadataProvider.SPY_PLAYER);

                                    localPlayer.toBukkit().setGameMode(GameMode.SURVIVAL);
                                    localPlayer.toBukkit().teleport(localPlayer.getLocation().subtract(0D, 1D, 0D));

                                    t.stop();
                                    return;
                                }

                                localPlayer.toBukkit().setNoDamageTicks(10);
                                localPlayer.toBukkit().setFallDistance(0);

                                passed.addAndGet(2);
                            }, 5L, 2L);

                    // spectator - end

                    localPlayer.msg("&6(Spy - Debug) &eYou are now spying on " + arrow.getName());
                });

        Events.subscribe(PlayerInteractEvent.class)
                .filter(AbilityPredicates.playerHasAbility(this))
                .filter(EventFilters.playerHasMetadata(MetadataProvider.SPY_PLAYER))
                .handler(e -> {
                    e.getPlayer().sendMessage("§6(Spy - Debug) §cYou can't interact while spying!");
                    e.setCancelled(true);
                });
    }

    private void sendCameraPacket(LocalPlayer localPlayer, Entity arrow) {
        final PacketContainer camera = new PacketContainer(PacketType.Play.Server.CAMERA);
        camera.getIntegers().write(0, arrow.getEntityId());

        try {
            KitPlugin.getSingleton().getProtocolManager().sendServerPacket(localPlayer.toBukkit(), camera);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(
                    "Cannot send packet " + camera, e);
        }

    }


    /*
    Events.subscribe(ProjectileHitEvent.class)
                .filter(e -> e.getEntity() instanceof Arrow)
                .filter(EventFilters.entityHasMetadata(MetadataProvider.SPY_ARROW))
                .handler(e -> {
                   final Arrow arrow = (Arrow) e.getEntity();
                   if(arrow.getShooter() == null) {
                       Logger.debug("[Spy] The arrow doesn't have a shooter!");
                       return;
                   }

                   final LocalPlayer localPlayer = LocalPlayer.get((Player) arrow.getShooter());
                   localPlayer.msg("&6(Spy - Debug) &aYour arrow has landed");

                   localPlayer.toBukkit().setSpectatorTarget(null);
                   localPlayer.toBukkit().setGameMode(GameMode.SURVIVAL);

                    localPlayer.toBukkit().teleport(arrow.getLocation().subtract(0D, 1D, 0D));
                    localPlayer.metadata().remove(MetadataProvider.SPY_PLAYER);
                });

    arrow.setShooter(localPlayer.toBukkit());
                    Metadata.provideForEntity(arrow).put(MetadataProvider.SPY_ARROW, SoftValue.of(true));

                    // spectator - start

                    localPlayer.metadata().put(MetadataProvider.SPY_PLAYER, SoftValue.of(true));
                    localPlayer.toBukkit().getLocation().setDirection(arrow.getVelocity());

                    localPlayer.toBukkit().setGameMode(GameMode.SPECTATOR);
                    localPlayer.toBukkit().setSpectatorTarget(arrow);

                    Location loc = localPlayer.getLocation();
                    Location arrowLoc = arrow.getLocation();

                    loc.setYaw(arrowLoc .getYaw());
                    loc.setPitch(arrowLoc .getPitch());
                    localPlayer.toBukkit().teleport(loc);

                    // spectator - end

                    localPlayer.msg("&6(Spy - Debug) &eYou are now spying on " + arrow.getName());
     */
}