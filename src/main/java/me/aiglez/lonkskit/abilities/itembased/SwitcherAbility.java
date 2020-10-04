package me.aiglez.lonkskit.abilities.itembased;

import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.Logger;
import me.aiglez.lonkskit.utils.items.ItemStackBuilder;
import me.lucko.helper.config.ConfigurationNode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.TimeUnit;

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
            localPlayer.msg("&cPlease wait, {0} second(s) left", cooldown.remainingTime(localPlayer, TimeUnit.SECONDS));
            e.setCancelled(true);
        }

    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent e) {
        if(e.getEntityType() != EntityType.SNOWBALL) return;
        final Snowball snowball = (Snowball) e.getEntity();
        if(isItemStack(snowball.getItem())) {
            final Entity hit = e.getHitEntity();
            if(hit == null) {
                Logger.debug("[Hit] The snowball didn't hit anything!");
                return;
            }
            if(snowball.hasMetadata("shooter")) {
                Logger.debug("[Hit] Shooter metadata found: " + snowball.getMetadata("shooter"));
            }

            if(snowball.getShooter() == null || !(snowball.getShooter() instanceof Player)) {
                return;
            }

            final LocalPlayer localPlayer = LocalPlayer.get((Player) snowball.getShooter());

            final Location playerLocation = localPlayer.toBukkit().getLocation();
            final Location hitLocation = hit.getLocation();

            localPlayer.toBukkit().teleport(hitLocation);
            hit.teleport(playerLocation);

            localPlayer.msg("&eYou have swapped locations with {0} ", hit.getName());
        }
    }
}
