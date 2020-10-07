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
                .name("&bFly")
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

        // start
        // wait 7 seconds > send 3 second warn
        // wait 1 second  > send 3 second warn
        // wait 1 second  > send 2 second warn
        // wait 1 second  > send 1 second warn
        // wait 1 second  > send no remaining time warn
        // end

        Schedulers.sync()
                .run(() -> {
                    localPlayer.msg("&b(Superman) &ayou can now fly for {0} second(s)", 10);
                    localPlayer.toBukkit().setAllowFlight(true);

                })

                .thenRunDelayedSync(() -> {
                    localPlayer.msg("&b(Superman) &eyou can fly for {0} more second(s)", 3);
                }, 7, TimeUnit.SECONDS)

                .thenRunDelayedSync(() -> {
                    localPlayer.msg("&b(Superman) &eyou can fly for {0} more second(s)", 2);
                }, 1, TimeUnit.SECONDS)

                .thenRunDelayedSync(() -> {
                    localPlayer.msg("&b(Superman) &eyou can fly for {0} more second(s)", 1);
                }, 1, TimeUnit.SECONDS)

                .thenRunDelayedSync(() -> {
                    localPlayer.msg("&b(Superman) &cyou can no longer fly");
                    localPlayer.toBukkit().setAllowFlight(false);
                }, 7, TimeUnit.SECONDS);


    }

    @Override
    public void whenLeftClicked(PlayerInteractEvent e) { }

    @Override
    public void handleListeners() { }
}
