package me.aiglez.lonkskit.abilities.external.itembased;


import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.lucko.helper.config.ConfigurationNode;
import me.lucko.helper.item.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
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
    public void whenClicked(PlayerInteractEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void whenClickPlayer(PlayerInteractEntityEvent event){
        LocalPlayer player = LocalPlayer.get(event.getPlayer());
        if (!this.cooldown.test(player)){
            player.msg("&cPlease wait, {0} second(s) left", cooldown.remainingTime(player, TimeUnit.SECONDS));
            return;

        }
        if (event.getRightClicked() instanceof Player) {
            Player target = (Player) event.getRightClicked();
            int a = new Random().nextInt(30);
            if (target.getInventory().getItem(a).getType() != Material.AIR) {
                ItemStack first = target.getInventory().getItemInMainHand(); // Player#getItemInHand is @deprecated
                ItemStack second = target.getInventory().getItem(a);
                target.getInventory().setItemInMainHand(second);
                target.getInventory().setItem(a, first);
            }

        }
    }
}
/*Leather Body, leather leggings, letherboots
<-- all unbreaking III, Protection III. Wood Sword:Unbreaking III Sharpness II. Blaze Rod. 34 soup.
(RENAME blaze rod 'Staff')
(If a player right clicks another player with a blaze rod, the target player's
item goes into their inventory in a random slot and the item in their hand is set to air
. If the target player's inventory is full, it swaps a random item in the target player's inventory with the item in their hand.)(5 second cooldown)*/