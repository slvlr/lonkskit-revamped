package me.aiglez.lonkskit.abilities.external.itembased;

import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.items.ItemStackBuilder;
import me.lucko.helper.config.ConfigurationNode;
import org.bukkit.Material;
import org.bukkit.entity.Fireball;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.TimeUnit;

public class ShadowbladeAbility extends ItemStackAbility {
    ItemStack item;
    protected ShadowbladeAbility(ConfigurationNode configuration) {
        super("shadowblade", configuration);
        this.item = ItemStackBuilder.of(Material.IRON_SWORD).build();
    }

    @Override
    public ItemStack getItemStack() {
        return this.item;
    }

    @Override
    public boolean isItemStack(ItemStack item) {
        return item.isSimilar(item);
    }

    @Override
    public void whenClicked(PlayerInteractEvent e) {
        LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());
        if (!this.cooldown.test(localPlayer)){
            localPlayer.msg("&cPlease wait, {0} second(s) left", cooldown.remainingTime(localPlayer, TimeUnit.SECONDS));
        }else {
            if (localPlayer.toBukkit().getInventory().getItemInMainHand() == this.item) {
                if (e.getAction() == Action.RIGHT_CLICK_AIR) {
                    localPlayer.toBukkit().launchProjectile(Fireball.class);
                }
            }
        }
    }
}