package me.aiglez.lonkskit.abilities.itembased;

import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.items.ItemStackBuilder;
import me.lucko.helper.Events;
import me.lucko.helper.config.ConfigurationNode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.TimeUnit;

/**
 * @author AigleZ
 * @date 02/10/2020
 */
public class SwitcherAbility extends ItemStackAbility {

    private final ItemStack item;
    // TODO: add 4 snowballs when killing a player

    public SwitcherAbility(ConfigurationNode configuration) {
        super("switcher", configuration);
        this.item = ItemStackBuilder.of(Material.SNOWBALL)
                .name("&dSwitchers")
                .amount(16)
                .build();
    }

    @Override
    public ItemStack getItemStack() { return item; }

    @Override
    public boolean isItemStack(ItemStack item) { return this.item.isSimilar(item); }

    // --------------------------------------------------------------------------------------------
    @Override
    public void whenUsed(PlayerInteractEvent e) {
        final LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());
        if(!cooldown.test(localPlayer)){
            localPlayer.msg("&b(Switcher) &cPlease wait, {0} second(s) left", cooldown.remainingTime(localPlayer, TimeUnit.SECONDS));
            e.setCancelled(true);
        }
    }

    @Override
    public void handleListeners() {
        Events.subscribe(ProjectileHitEvent.class)
                .filter(e -> e.getEntityType() == EntityType.SNOWBALL)
                .handler(e -> {
                    final Snowball snowball = (Snowball) e.getEntity();
                    if(!isItemStack(snowball.getItem()) || snowball.getShooter() == null || !(snowball.getShooter() instanceof Player)) return;

                    final Entity hit = e.getHitEntity();
                    if(hit == null) return;

                    final LocalPlayer localPlayer = LocalPlayer.get((Player) snowball.getShooter());
                    final Location playerLocation = localPlayer.toBukkit().getLocation();
                    final Location hitLocation = hit.getLocation();

                    localPlayer.toBukkit().teleport(hitLocation);
                    hit.teleport(playerLocation);

                    localPlayer.msg("&b(Switcher) &fYou have swapped locations with {0} ", hit.getName());
                });
    }
}
