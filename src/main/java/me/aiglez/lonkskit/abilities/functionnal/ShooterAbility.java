package me.aiglez.lonkskit.abilities.functionnal;

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

                    HashMap<Integer, ItemStack> map = inventory.removeItem(new ItemStack(Material.ARROW, 5));
                    int toRemove = 5;
                    if(!map.isEmpty()) {
                        localPlayer.msg("&e(Shooter) seems like you don't have 5 arrows:: (you need {0} more)", map.size());
                        toRemove = 5 - map.size();
                    }

                    for (int i = 0; i < toRemove; i++) {
                        Schedulers.sync()
                                .runLater(() -> {
                                    launchArrows(localPlayer);
                                }, 1L);
                    }
                });
    }

    private void launchArrows(LocalPlayer localPlayer) {
        localPlayer.toBukkit().launchProjectile(Arrow.class);
    }
}
