package me.aiglez.lonkskit.abilities.itembased;

import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.Logger;
import me.aiglez.lonkskit.utils.MetadataProvider;
import me.lucko.helper.Events;
import me.lucko.helper.config.ConfigurationNode;
import me.lucko.helper.event.filter.EventFilters;
import me.lucko.helper.metadata.SoftValue;
import org.bukkit.GameMode;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * @author AigleZ
 * @date 10/10/2020
 */
public class SpyAbility extends ItemStackAbility {

    public SpyAbility(ConfigurationNode configuration) {
        super("spy", configuration);
    }

    @Override
    public void whenRightClicked(PlayerInteractEvent e) {

    }

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

                    applyEffects(localPlayer);

                    // spectator - start

                    localPlayer.metadata().put(MetadataProvider.SPY_PLAYER, SoftValue.of(true));
                    localPlayer.toBukkit().setGameMode(GameMode.SPECTATOR);
                    localPlayer.toBukkit().setSpectatorTarget(arrow);

                    Logger.debug("[Spy] Spectator target: {0}", (localPlayer.toBukkit().getSpectatorTarget() == null ? "Not Found" : localPlayer.toBukkit().getSpectatorTarget().getName()));

                    // spectator - end

                    localPlayer.msg("&6(Spy - Debug) &eYou are now spying on " + arrow.getName());
                });

        /*
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
                    e.getPlayer().sendMessage("§6(Spy - Debug) §cWait until the arrow lands!");
                    //e.setCancelled(true);
                });

         */

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

                    localPlayer.toBukkit().setSpectatorTarget(null);
                    localPlayer.toBukkit().setGameMode(GameMode.SURVIVAL);

                    localPlayer.toBukkit().teleport(arrow.getLocation().subtract(0D, 1D, 0D));
                    localPlayer.metadata().remove(MetadataProvider.SPY_PLAYER);

                    localPlayer.msg("&6(Spy - Debug) &aYour arrow has landed");
                });
    }


    /*


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
