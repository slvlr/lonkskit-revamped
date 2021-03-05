package me.aiglez.lonkskit.abilities.functional;

import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.FunctionalAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.config.yaml.YAMLConfigurationLoader;
import org.bukkit.Material;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author AigleZ
 * @date 05/10/2020
 */

public class ShooterAbility extends FunctionalAbility {


    public ShooterAbility(YAMLConfigurationLoader configurationLoader) throws IOException {
        super("shooter", configurationLoader);
    }

    @Override
    public void registerListeners() {
        Events.subscribe(PlayerInteractEvent.class)
                .filter(AbilityPredicates.hasAbility(this))
                .filter(e -> e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)
                .filter(PlayerInteractEvent::hasItem)
                .filter(e -> e.getItem().getType() == Material.ARROW)
                .handler(e -> {
                    final LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());
                    if (!cooldown.test(localPlayer)){
                        localPlayer.msg("&b[LonksKit] &cPlease wait, {0} second(s) left", cooldown.remainingTime(localPlayer, TimeUnit.SECONDS));
                        return;
                    }
                    applyEffects(localPlayer);

                    final HashMap<Integer, ItemStack> map = localPlayer.toBukkit().getInventory().removeItem(new ItemStack(Material.ARROW, 5));
                    int toRemove = 5;
                    if(!map.isEmpty()) {
                        toRemove = 5 - map.size();
                    }

                    final long delay = configuration.getNode("delay-between-arrows").getLong(1L);

                    final AtomicInteger launched = new AtomicInteger(0);
                    final int finalToRemove = toRemove;
                    Schedulers.sync()
                            .runRepeating(t -> {
                                if(launched.intValue() == finalToRemove) {
                                    t.stop();
                                    return;
                                }
                                if (localPlayer.toBukkit().getInventory().contains(Material.ARROW)) {
                                    launchArrows(localPlayer);
                                    launched.incrementAndGet();
                                }else t.stop();
                            }, 1L, delay);
                });
        Events.subscribe(PlayerDeathEvent.class)
                .filter(e -> e.getEntity().getKiller() != null)
                .filter(AbilityPredicates.isKillerhaveAbility(this))
                .handler(e -> {
                    if (!addItems(e.getEntity().getKiller().getInventory(),Material.ARROW,4)){
                        e.getEntity().getKiller().getInventory().addItem(new ItemStack(Material.ARROW,4));
                    }

                });
        Events.subscribe(EntityPickupItemEvent.class, EventPriority.HIGHEST)
                .filter(e -> e.getEntity() instanceof Player &&  e.getItem() instanceof Arrow)
                .handler(e -> e.setCancelled(true));
    }

    private void launchArrows(LocalPlayer localPlayer) {
        Arrow arrow =  localPlayer.toBukkit().launchProjectile(Arrow.class);
        arrow.setShooter(localPlayer.toBukkit());
        arrow.setInvulnerable(true);
        arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
        if (localPlayer.toBukkit().getInventory().contains(Material.ARROW)){
            removeItems(localPlayer.toBukkit().getInventory(),Material.ARROW,1);
        }
    }
    public static void removeItems(PlayerInventory inventory, Material type, int amount) {
        if (amount <= 0) return;
        int size = inventory.getSize();
        for (int slot = 0; slot < size; slot++) {
            ItemStack is = inventory.getItem(slot);
            if (is == null) continue;
            if (type == is.getType()) {
                int newAmount = is.getAmount() - amount;
                if (newAmount > 0) {
                    is.setAmount(newAmount);
                    break;
                } else {
                    inventory.clear(slot);
                    amount = -newAmount;
                    if (amount == 0) break;
                }
            }
        }
    }
    public static boolean addItems(PlayerInventory inventory, Material type, int amount) {
        if (amount <= 0) return false;
        int size = inventory.getSize();
        for (int slot = 0; slot < size; slot++) {
            ItemStack is = inventory.getItem(slot);
            if (is != null) {
                if (type == is.getType() && is.getAmount() < 64) {
                    int newAmount = (is.getAmount() + amount) <= 64 ? is.getAmount() + amount : 0;
                    if (newAmount > 0) {
                        is.setAmount(newAmount);
                        return true;
                    } else {
                        inventory.clear(slot);
                        amount = -newAmount;
                        if (amount == 0) {
                            return false;
                        }
                    }
                }
            }
        }
        return false;
    }

}
