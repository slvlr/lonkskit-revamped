package me.aiglez.lonkskit.abilities.itembased;

import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.lucko.helper.Events;
import me.lucko.helper.config.ConfigurationNode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.concurrent.TimeUnit;

/**
 * @author AigleZ
 * @date 02/10/2020
 */
public class SwitcherAbility extends ItemStackAbility {
    // TODO: add 4 snowballs when killing a player

    public SwitcherAbility(ConfigurationNode configuration) {
        super("switcher", configuration);
    }

    // --------------------------------------------------------------------------------------------
    @Override
    public void whenRightClicked(PlayerInteractEvent e) {
        final LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());
        if(!cooldown.test(localPlayer)){
            localPlayer.msg("&b[LonksKit] &cPlease wait, {0} second(s) left", cooldown.remainingTime(localPlayer, TimeUnit.SECONDS));
            e.setCancelled(true);
        }

        applyEffects(localPlayer);
    }

    @Override
    public void whenLeftClicked(PlayerInteractEvent e) {
        e.setCancelled(true);
    }

    @Override
    public void registerListeners() {
        Events.subscribe(ProjectileHitEvent.class)
                .filter(e -> e.getEntityType() == EntityType.SNOWBALL)
                .handler(e -> {
                    final Snowball snowball = (Snowball) e.getEntity();
                    if(!isItemStack(snowball.getItem()) || snowball.getShooter() == null || !(snowball.getShooter() instanceof Player)) return;

                    final Entity hit = e.getHitEntity();
                    if(!(hit instanceof Player)) return;

                    final LocalPlayer localPlayer = LocalPlayer.get((Player) snowball.getShooter());
                    final Location playerLocation = localPlayer.toBukkit().getLocation();
                    final Location hitLocation = hit.getLocation();

                    localPlayer.toBukkit().teleport(hitLocation);
                    hit.teleport(playerLocation);

                    localPlayer.msg(configuration.getNode("messages", "switcher").getString("Message switched Null"), hit.getName());
                });
    }
}
