package me.aiglez.lonkskit.abilities.external.itembased;


import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.lucko.helper.Events;
import me.lucko.helper.config.ConfigurationNode;
import me.lucko.helper.item.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;
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
        e.setCancelled(true);
        if(!cooldown.test(LocalPlayer.get(e.getPlayer()))){
            LocalPlayer.get(e.getPlayer()).msg("&3(Thor) &cPlease wait, {0} second(s) left", cooldown.remainingTime(LocalPlayer.get(e.getPlayer()), TimeUnit.SECONDS));
            return;
        }

    }

    @Override
    public void handleListeners() {
        Events.subscribe(PlayerInteractEntityEvent.class)
                .filter(AbilityPredicates.playerHasAbility(this))
                .filter(o -> o.getRightClicked() instanceof Player)
                .handler(e -> {
                        Player target = (Player) e.getRightClicked();
                        int a = new Random().nextInt(30);
                        if (target.getInventory().getItem(a).getType() != Material.AIR) {
                            ItemStack first = target.getInventory().getItemInMainHand(); // Player#getItemInHand is @deprecated
                            ItemStack second = target.getInventory().getItem(a);
                            target.getInventory().setItemInMainHand(second);
                            target.getInventory().setItem(a, first);
                    }else {
                            ItemStack itemMain = target.getInventory().getItemInMainHand();
                            target.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                            target.getInventory().addItem(itemMain);
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