package me.aiglez.lonkskit.abilities.itembased;

import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.items.ItemStackBuilder;
import me.lucko.helper.Schedulers;
import me.lucko.helper.config.ConfigurationNode;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.TimeUnit;

public class SupermanAbility extends ItemStackAbility {

    private final ItemStack item;

    public SupermanAbility(ConfigurationNode configuration) {
        super("superman", configuration);
        this.item = ItemStackBuilder.of(Material.FEATHER)
                .name(configuration.getNode("item-name").getString("Fly"))
                .build();
    }

    @Override
    public ItemStack getItemStack() { return this.item; }

    @Override
    public boolean isItemStack(ItemStack item) { return this.item.isSimilar(item); }

    @Override
    public void whenRightClicked(PlayerInteractEvent e) {
        e.setCancelled(true);
        final LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());

        if(!cooldown.test(localPlayer)){
            localPlayer.msg("&b[LonksKit] &cPlease wait, {0} second(s) left", cooldown.remainingTime(localPlayer, TimeUnit.SECONDS));
            return;
        }

        // start
        // wait 7 seconds > send 3 second warn
        // wait 1 second  > send 3 second warn
        // wait 1 second  > send 2 second warn
        // wait 1 second  > send 1 second warn
        // wait 1 second  > send no remaining time warn
        // end

        Schedulers.sync()
                .run(() -> {
                    localPlayer.msg(configuration.getNode("messages", "start").getString("Message start Null"));
                    localPlayer.toBukkit().setAllowFlight(true);

                })

                .thenRunDelayedSync(() -> {
                    localPlayer.msg(configuration.getNode("messages", "three-sec-remaining").getString("Message Three sec remaining Null"));
                }, 7, TimeUnit.SECONDS)

                .thenRunDelayedSync(() -> {
                    localPlayer.msg(configuration.getNode("messages", "two-sec-remaining").getString("Message Two sec remaining Null"));
                }, 1, TimeUnit.SECONDS)

                .thenRunDelayedSync(() -> {
                    localPlayer.msg(configuration.getNode("messages", "one-sec-remaining").getString("Message One sec remaining Null"));
                }, 1, TimeUnit.SECONDS)

                .thenRunDelayedSync(() -> {
                    localPlayer.msg(configuration.getNode("messages", "end").getString("Message end Null"));
                    localPlayer.toBukkit().setAllowFlight(false);
                }, 1, TimeUnit.SECONDS);


    }

    @Override
    public void whenLeftClicked(PlayerInteractEvent e) {
        e.setCancelled(true);
    }

    @Override
    public void handleListeners() { }
}
