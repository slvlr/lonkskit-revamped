package me.aiglez.lonkskit.abilities.external.itembased;

import me.aiglez.lonkskit.KitPlugin;
import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.items.ItemStackBuilder;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.config.ConfigurationNode;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

public class ElderAbility extends ItemStackAbility {
    ItemStack item;
    public ElderAbility(ConfigurationNode configuration) {
        super("elder", configuration);
        this.item = ItemStackBuilder.of(Material.BLAZE_ROD)
                .name(Objects.requireNonNull(getConfiguration().getNode("item-name").getString()))
                .build();
    }

    @Override
    public ItemStack getItemStack() {
        return item;
    }

    @Override
    public boolean isItemStack(ItemStack item) {
        return this.item.isSimilar(item);
    }

    @Override
    public void whenRightClicked(PlayerInteractEvent e) {


    }

    @Override
    public void whenLeftClicked(PlayerInteractEvent e) {

    }

    @Override
    public void handleListeners() {
        Events.subscribe(PlayerInteractEntityEvent.class)
                .filter(AbilityPredicates.playerHasAbility(this))
                .filter(event -> event.getRightClicked() instanceof Player)
                .handler(event -> {
                    LocalPlayer localPlayer = LocalPlayer.get(event.getPlayer());
                    Player victim = (Player) event.getRightClicked();
                    if (!cooldown.test(localPlayer)){
                        localPlayer.msg("&e(Sonic) &cPlease wait, {0} second(s) left", cooldown.remainingTime(localPlayer, TimeUnit.SECONDS));
                        return;
                    }
                    work(victim);

                });


    }
    public void work(Player player){
        int index = RandomUtils.nextInt(4);
        ItemStack[] armor = {player.getInventory().getHelmet(),player.getInventory().getChestplate(),player.getInventory().getLeggings(),player.getInventory().getBoots()};
        switch (index){
            case 0:
                clearArmor(player);
                player.getInventory().setChestplate(armor[1]);
                player.getInventory().setLeggings(armor[2]);
                player.getInventory().setBoots(armor[3]);
                break;
            case 1:
                clearArmor(player);
                player.getInventory().setHelmet(armor[0]);
                player.getInventory().setLeggings(armor[2]);
                player.getInventory().setBoots(armor[3]);
                break;
            case 2:
                clearArmor(player);
                player.getInventory().setHelmet(armor[0]);
                player.getInventory().setChestplate(armor[1]);
                player.getInventory().setBoots(armor[3]);
                break;
            case 3:
                clearArmor(player);
                player.getInventory().setChestplate(armor[1]);
                player.getInventory().setLeggings(armor[2]);
                player.getInventory().setHelmet(armor[0]);
                break;
        }
        Schedulers.sync().runLater(() -> {
            ItemStack[] armor1 = {player.getInventory().getHelmet(),player.getInventory().getChestplate(),player.getInventory().getLeggings(),player.getInventory().getBoots()};
            clearArmor(player);
            player.getInventory().setHelmet(armor1[0]);
            player.getInventory().setChestplate(armor1[1]);
            player.getInventory().setLeggings(armor1[2]);
            player.getInventory().setBoots(armor1[3]);
        },200L);
    }
    public static void clearArmor(Player player){
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
    }
}
/*Elder: Leatherbody, leather leggings, leather boots <-- all Protection III. Woodsword:Unbreaking III Sharpness II. Blaze rod. 34 soup.
(RENAME blaze rod 'Staff
')(If a player right clicks another player with a blaze rod,  will remove a random piece of the targets armor for 10 seconds. 15 seconds cooldown*/