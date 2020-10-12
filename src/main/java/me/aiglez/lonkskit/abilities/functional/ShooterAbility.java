package me.aiglez.lonkskit.abilities.functional;

import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.FunctionalAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.config.ConfigurationNode;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author AigleZ
 * @date 05/10/2020
 */
public class ShooterAbility extends FunctionalAbility {

    public ShooterAbility(ConfigurationNode configuration) {
        super("shooter", configuration);
    }

    @Override
    public void handleListeners() {
        Events.subscribe(PlayerInteractEvent.class)
                .filter(AbilityPredicates.playerHasAbility(this))
                .filter(e -> e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)
                .filter(PlayerInteractEvent::hasItem)
                .filter(e -> e.getItem().getType() == Material.ARROW)
                .handler(e -> {
                    final LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());
                    final PlayerInventory inventory = localPlayer.toBukkit().getInventory();

                    final HashMap<Integer, ItemStack> map = inventory.removeItem(new ItemStack(Material.ARROW, 5));
                    int toRemove = 5;
                    if(!map.isEmpty()) {
                        localPlayer.msg("&e(Debug - Shooter) seems like you don't have 5 arrows:: (you need {0} more)", map.size());
                        toRemove = 5 - map.size();
                    }

                    final long delay = configuration.getNode("delay-between-arrows").getLong(1L);
                    localPlayer.msg("&e(Debug - Shooter) Delay (ticks): " + delay);
                    AtomicInteger launched = new AtomicInteger(0);
                    final int finalToRemove = toRemove;
                    Schedulers.sync()
                            .runRepeating(t -> {
                                if(launched.intValue() == finalToRemove) {
                                    t.stop();
                                    return;
                                }

                                launchArrows(localPlayer);
                                launched.incrementAndGet();
                            }, 1L, delay);
                });
    }

    private void launchArrows(LocalPlayer localPlayer) {
        localPlayer.toBukkit().launchProjectile(Arrow.class);
    }
}
