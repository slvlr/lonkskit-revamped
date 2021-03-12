package me.aiglez.lonkskit.abilities.itembased;
import com.destroystokyo.paper.event.player.PlayerStopSpectatingEntityEvent;
import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.MetadataProvider;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.config.yaml.YAMLConfigurationLoader;
import me.lucko.helper.event.filter.EventFilters;
import me.lucko.helper.metadata.ExpiringValue;
import me.lucko.helper.metadata.Metadata;
import me.lucko.helper.metadata.SoftValue;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
public class SpyAbility extends ItemStackAbility {

    public SpyAbility(YAMLConfigurationLoader yamlConfigurationLoader) throws IOException {
        super("spy", yamlConfigurationLoader);
    }

    @Override
    public void whenRightClicked(PlayerInteractEvent e) { }

    @Override
    public void whenLeftClicked(PlayerInteractEvent e) {

    }

    @Override
    public void registerListeners() {
        Events.subscribe(EntityShootBowEvent.class)
                .filter(AbilityPredicates.possiblyHasAbility(this))
                .filter(e -> isItemStack(e.getBow()))
                .filter(e -> e.getProjectile() instanceof Arrow)
                .handler(e -> {
                    final LocalPlayer localPlayer = LocalPlayer.get((Player) e.getEntity());
                    final Arrow arrow = (Arrow) e.getProjectile();
                    arrow.setShooter(localPlayer.toBukkit());
                    Metadata.provideForEntity(arrow).put(MetadataProvider.SPY_ARROW, SoftValue.of(true));

                    applyEffects(localPlayer);

                    // spectator - start

                    Schedulers.sync()
                            .runLater(() -> {
                                if (!e.getProjectile().isOnGround()) {
                                    localPlayer.getMetadata().put(MetadataProvider.SPY_PLAYER, ExpiringValue.of(true, 100, TimeUnit.SECONDS));
                                    localPlayer.toBukkit().setGameMode(GameMode.SPECTATOR);
                                    localPlayer.toBukkit().setSpectatorTarget(arrow);
                                }
                                if (e.getProjectile().getLocation().getY() <= 0) e.getEntity().setHealth(0);
                            }, 2L);
                    Schedulers.sync()
                            .runRepeating(t -> {
                                if (arrow.isDead() || !arrow.isValid()){
                                    t.stop();
                                    t.close();
                                }else {
                                    if (arrow.getLocation().getY() <= 0){
                                        ((Player)arrow.getShooter()).setHealth(0);
                                        LocalPlayer.get(((Player)arrow.getShooter())).setInKP(true);
                                        LocalPlayer.get(((Player)arrow.getShooter())).setSelectedKit(null);
                                        ((Player)arrow.getShooter()).setGameMode(GameMode.SURVIVAL);
                                        arrow.remove();
                                        t.stop();
                                        t.close();
                                    }
                                }
                            },10L,10L);
                    // spectator - end

                    localPlayer.msg("&6(Spy - Debug) &eYou are now spying on " + arrow.getName());
                });

        Events.subscribe(PlayerMoveEvent.class)
                .filter(e -> e.getPlayer().getLocation().getY() < 1)
                .handler(e -> {
                    LocalPlayer.get(e.getPlayer()).getMetadata().remove(MetadataProvider.SPY_PLAYER);
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gamemode survival " + e.getPlayer().getName());
                    System.out.println("YEP SPY");
                });

        Events.subscribe(PlayerInteractEvent.class)
                .filter(AbilityPredicates.hasAbility(this))
                .filter(AbilityPredicates.hasMetadata(MetadataProvider.SPY_PLAYER))
                .handler(e -> {
                    e.getPlayer().sendMessage("§6(Spy - Debug) §cYou can't interact while spying!");
                    e.setCancelled(true);
                });
        Events.subscribe(PlayerStopSpectatingEntityEvent.class)
                .filter(AbilityPredicates.hasAbility(this))
                .filter(AbilityPredicates.hasMetadata(MetadataProvider.SPY_PLAYER))
                .handler(e -> {
                    if(e.getSpectatorTarget().getUniqueId().equals(e.getPlayer().getUniqueId())) {
                        e.getPlayer().sendMessage("§6(Spy - Debug) §eYou stopped spectating ur self (useless)");
                        return;
                    }
                    e.setCancelled(true);
                });

        Events.subscribe(ProjectileHitEvent.class)
                .filter(e -> e.getEntity() instanceof Arrow)
                .filter(EventFilters.entityHasMetadata(MetadataProvider.SPY_ARROW))
                .handler(e -> {
                    long start = System.currentTimeMillis();
                    final Arrow arrow = (Arrow) e.getEntity();
                    arrow.setDamage(0);
                    if(arrow.getShooter() == null) {
                        return;
                    }
                    final LocalPlayer localPlayer = LocalPlayer.get((Player) arrow.getShooter());
                    Schedulers.sync().runLater(() -> {
                        try {
                            if (localPlayer.toBukkit().getGameMode() == GameMode.SPECTATOR){
                                localPlayer.getMetadata().remove(MetadataProvider.SPY_PLAYER);
                                if (localPlayer.toBukkit().getSpectatorTarget() != null){
                                    localPlayer.toBukkit().setSpectatorTarget(null);
                                }
                                localPlayer.toBukkit().setGameMode(GameMode.SURVIVAL);
                                if (e.getHitEntity() != null) localPlayer.toBukkit().teleport(e.getHitEntity().getLocation().add(0D, 1D, 0D));
                                else if (e.getHitBlock() != null) localPlayer.toBukkit().teleport(e.getHitBlock().getLocation().add(0D, 1D, 0D));

                            }
                        }catch (IllegalStateException ignored){}

                    },10L);
                    try {
                        if (localPlayer.toBukkit().getGameMode() == GameMode.SPECTATOR){
                            localPlayer.getMetadata().remove(MetadataProvider.SPY_PLAYER);
                            if (localPlayer.toBukkit().getSpectatorTarget() != null){
                                localPlayer.toBukkit().setSpectatorTarget(null);
                            }
                            localPlayer.toBukkit().setGameMode(GameMode.SURVIVAL);
                            if (e.getHitEntity() != null) localPlayer.toBukkit().teleport(e.getHitEntity().getLocation().add(0D, 1D, 0D));
                            else if (e.getHitBlock() != null) localPlayer.toBukkit().teleport(e.getHitBlock().getLocation().add(0D, 1D, 0D));

                        }
                    }catch (IllegalStateException ignored){}

                    localPlayer.msg("&6(Spy - Debug) &aYour arrow has landed (handle time: {0}ms)",
                            System.currentTimeMillis() - start);
                });

      Events.subscribe(PlayerRespawnEvent.class)
                .filter(e -> e.getPlayer().getGameMode() == GameMode.SPECTATOR)
                .handler(e -> Schedulers
                        .builder()
                        .sync()
                        .after(20L)
                        .run(() -> {
                            LocalPlayer.get(e.getPlayer()).getMetadata().remove(MetadataProvider.SPY_PLAYER);
                            if (e.getPlayer().getGameMode() == GameMode.SPECTATOR ) {
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gamemode survival " + e.getPlayer().getName());
                            }
                        }));
        Events.subscribe(AsyncPlayerChatEvent.class)
                .handler(e -> System.out.println(e.getMessage()));
    }
}