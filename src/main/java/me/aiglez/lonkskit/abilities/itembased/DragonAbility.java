package me.aiglez.lonkskit.abilities.itembased;

import me.aiglez.lonkskit.WorldProvider;
import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.MetadataProvider;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.config.ConfigurationNode;
import me.lucko.helper.metadata.ExpiringValue;
import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.concurrent.TimeUnit;

/**
 * @author AigleZ
 * @date 01/10/2020 at 13:50
 */
public class DragonAbility extends ItemStackAbility {

    public DragonAbility(ConfigurationNode configuration) {
        super("dragon", configuration);
    }

    // --------------------------------------------------------------------------------------------
    @Override
    public void whenRightClicked(PlayerInteractEvent e) {
        e.setCancelled(true);
        final LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());
        if(!cooldown.test(localPlayer)){
            localPlayer.msg("&b[LonksKit] &cPlease wait, {0} second(s) left", cooldown.remainingTime(localPlayer, TimeUnit.SECONDS));
            return;
        }

        applyEffects(localPlayer);
        localPlayer.metadata().put(MetadataProvider.PLAYER_NO_FALL_DAMAGE, ExpiringValue.of(true, 10, TimeUnit.SECONDS));

        final Vector vec = localPlayer.toBukkit().getLocation().getDirection().multiply(
                configuration.getNode("strength").getDouble(5D)
        );
        localPlayer.toBukkit().setVelocity(vec);

        Schedulers.sync()
                .runRepeating(task -> {
                    if(!localPlayer.metadata().has(MetadataProvider.PLAYER_NO_FALL_DAMAGE)) {
                        task.stop();
                        return;
                    }

                    if(!localPlayer.toBukkit().isFlying() && localPlayer.getLocation().subtract(0D, 1D, 0D).getBlock().getType().isSolid()) {
                        task.stop();
                        return;
                    }

                    WorldProvider.KP_WORLD.playEffect(localPlayer.getLocation(), Effect.MOBSPAWNER_FLAMES, 0);
                }, 10L, 8L);

        localPlayer.msg("&b(Debug - Dragon) You have been pushed (strength: {0})", configuration.getNode("strength").getDouble(5D));
    }

    @Override
    public void whenLeftClicked(PlayerInteractEvent e) { }

    @Override
    public void registerListeners() {
        Events.subscribe(EntityDamageEvent.class)
                .filter(e -> e.getCause() == EntityDamageEvent.DamageCause.FALL)
                .filter(AbilityPredicates.possiblyHasAbility(this))
                .filter(AbilityPredicates.possiblyHasMetadata(MetadataProvider.PLAYER_NO_FALL_DAMAGE))
                .handler(e -> {
                    final LocalPlayer localPlayer = LocalPlayer.get((Player) e.getEntity());
                    localPlayer.metadata().remove(MetadataProvider.PLAYER_NO_FALL_DAMAGE);
                    e.setCancelled(true);
                });
    }
}
