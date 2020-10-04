package me.aiglez.lonkskit.abilities.itembased;

import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.lucko.helper.Schedulers;
import me.lucko.helper.config.ConfigurationNode;
import me.aiglez.lonkskit.utils.items.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.concurrent.TimeUnit;

public class PortastompAbility extends ItemStackAbility {

    private final ItemStack item;
    private final int MAX_HEIGHT = 125;

    public PortastompAbility(ConfigurationNode configuration) {
        super("portastomp", configuration);
        this.item = ItemStackBuilder.of(Material.ENDER_EYE)
                .name("&cPort")
                .build();
    }

    @Override
    public ItemStack getItemStack() { return this.item; }

    @Override
    public boolean isItemStack(ItemStack item) { return this.item.isSimilar(item); }

    // --------------------------------------------------------------------------------------------
    @Override
    public void whenUsed(PlayerInteractEvent e) {
        e.setCancelled(true);
        final LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());

        if(!cooldown.test(localPlayer)){
            localPlayer.msg("&cPlease wait, {0} second(s) left", cooldown.remainingTime(localPlayer, TimeUnit.SECONDS));
            return;
        }

        Schedulers.sync().runLater(() -> {
            //localPlayer.toBukkit().teleport(localPlayer.getLocation().clone().add(0D, 60D, 0D));

            localPlayer.toBukkit().setVelocity(new Vector(0, 10, 0));
            }, 2L);
    }
}
