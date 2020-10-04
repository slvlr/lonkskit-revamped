package me.aiglez.lonkskit.abilities.external.itembased;


import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.lucko.helper.Events;
import me.lucko.helper.config.ConfigurationNode;
import me.lucko.helper.item.ItemStackBuilder;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.TimeUnit;

public class MonkAbility extends ItemStackAbility {

    private final ItemStack item;

    public MonkAbility(ConfigurationNode configuration) {
        super("monk", configuration);
        this.item = ItemStackBuilder.of(Material.BLAZE_ROD)
                .name("&Staff")
                .build();
    }

    @Override
    public ItemStack getItemStack() {
        return this.item;
    }

    @Override
    public boolean isItemStack(ItemStack item) {
        return this.item.isSimilar(item);
    }

    @Override
    public void whenUsed(PlayerInteractEvent e) {
    }

    @Override
    public void handleListeners() {
        Events.subscribe(PlayerInteractEntityEvent.class)
                .filter(AbilityPredicates.playerHasAbility(this))
                .filter(o -> o.getRightClicked() instanceof Player)
                .handler(e -> {
                    final LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());
                    if(!cooldown.test(localPlayer)){
                        LocalPlayer.get(e.getPlayer()).msg("&5(Monk) &cPlease wait, {0} second(s) left", cooldown.remainingTime(LocalPlayer.get(e.getPlayer()), TimeUnit.SECONDS));
                        e.setCancelled(true);
                        return;
                    }

                    final Player rightClicked = (Player) e.getRightClicked();
                    int index = RandomUtils.nextInt(30); // RandomUtils is more optimized then created a new Random object each time

                    final ItemStack itemAtIndex = rightClicked.getInventory().getItem(index);
                    final ItemStack itemInMainHand = rightClicked.getInventory().getItemInMainHand(); // Player#getItemInHand is @deprecated
                    if (itemAtIndex != null && itemAtIndex.getType() != Material.AIR) {
                        rightClicked.getInventory().setItemInMainHand(itemAtIndex);
                        rightClicked.getInventory().setItem(index, itemInMainHand);
                    } else {
                        rightClicked.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                        rightClicked.getInventory().addItem(itemInMainHand);

                    }
                });
    }
}
/*Leather Body, leather leggings, letherboots
<-- all unbreaking III, Protection III. Wood Sword:Unbreaking III Sharpness II. Blaze Rod. 34 soup.
(RENAME blaze rod 'Staff')
(If a player right clicks another player with a blaze rod, the target player's
item goes into their inventory in a random slot and the item in their hand is set to air
. If the target player's inventory is full, it swaps a random item in the target player's inventory with the item in their hand.)(5 second cooldown)*/