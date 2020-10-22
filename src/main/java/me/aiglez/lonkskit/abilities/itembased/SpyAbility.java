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
import org.bukkit.GameMode;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author AigleZ
 * @date 10/10/2020
 */
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
                                localPlayer.metadata().put(MetadataProvider.SPY_PLAYER, ExpiringValue.of(true, 1, TimeUnit.SECONDS));
                                localPlayer.toBukkit().setGameMode(GameMode.SPECTATOR);
                                localPlayer.toBukkit().setSpectatorTarget(arrow);
                                }, 2L);
                    // spectator - end

                    localPlayer.msg("&6(Spy - Debug) &eYou are now spying on " + arrow.getName());
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
                    e.getPlayer().sendMessage("§6(Spy - Debug) §cWait until the arrow lands!");
                    e.setCancelled(true);
                });

        Events.subscribe(ProjectileHitEvent.class)
                .filter(e -> e.getEntity() instanceof Arrow)
                .filter(EventFilters.entityHasMetadata(MetadataProvider.SPY_ARROW))
                .handler(e -> {
                    long start = System.currentTimeMillis();

                    final Arrow arrow = (Arrow) e.getEntity();
                    if(arrow.getShooter() == null) {
                        return;
                    }

                    final LocalPlayer localPlayer = LocalPlayer.get((Player) arrow.getShooter());

                    localPlayer.metadata().remove(MetadataProvider.SPY_PLAYER);
                    localPlayer.toBukkit().setSpectatorTarget(null);
                    localPlayer.toBukkit().setGameMode(GameMode.SURVIVAL);
                    localPlayer.toBukkit().teleport(arrow.getLocation().subtract(0D, 1D, 0D));

                    localPlayer.msg("&6(Spy - Debug) &aYour arrow has landed (handle time: {0}ms)",
                            System.currentTimeMillis() - start);
                });

        Events.subscribe(PlayerDeathEvent.class)
                .filter(AbilityPredicates.possiblyHasMetadata(MetadataProvider.SPY_ARROW))
                .handler(e -> {
                    final LocalPlayer localPlayer = LocalPlayer.get(e.getEntity());
                    localPlayer.metadata().remove(MetadataProvider.SPY_PLAYER);
                    localPlayer.toBukkit().setSpectatorTarget(null);
                    localPlayer.toBukkit().setGameMode(GameMode.SURVIVAL);
                });

    }
}
