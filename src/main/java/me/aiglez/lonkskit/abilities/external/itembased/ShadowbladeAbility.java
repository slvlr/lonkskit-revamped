package me.aiglez.lonkskit.abilities.external.itembased;

import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.items.ItemStackBuilder;
import me.lucko.helper.config.ConfigurationNode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.TimeUnit;

public class ShadowbladeAbility extends ItemStackAbility {

    private final ItemStack item;

    public ShadowbladeAbility(ConfigurationNode configuration) {
        super("shadowblade", configuration);
        this.item = ItemStackBuilder.of(Material.IRON_SWORD).build();
    }

    @Override
    public ItemStack getItemStack() {
        return this.item;
    }

    @Override
    public boolean isItemStack(ItemStack item) {
        return this.item.isSimilar(item); // this.item != item | this.item returns the field itemstack & item returns the parameter itemstack !!!!
    }

    @Override
    public void whenUsed(PlayerInteractEvent e) {
        LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());
        if (!this.cooldown.test(localPlayer)){
            localPlayer.msg("&cPlease wait, {0} second(s) left", cooldown.remainingTime(localPlayer, TimeUnit.SECONDS));
            e.setCancelled(true);
            return;
        }
        if (e.getAction() == Action.RIGHT_CLICK_AIR) {
            Location eye = e.getPlayer().getEyeLocation();
            Location loc = eye.add(eye.getDirection().multiply(1.2));
            Fireball fireball = (Fireball) loc.getWorld().spawnEntity(loc, EntityType.FIREBALL);
            fireball.setVelocity(loc.getDirection().normalize().multiply(2));
            fireball.setShooter(e.getPlayer());
        }
    }

    @Override
    public void handleListeners() {

    }
}
