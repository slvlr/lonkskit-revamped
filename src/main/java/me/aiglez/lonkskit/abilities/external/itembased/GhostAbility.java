package me.aiglez.lonkskit.abilities.external.itembased;

import me.aiglez.lonkskit.KitPlugin;
import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.items.ItemStackBuilder;
import me.lucko.helper.config.ConfigurationNode;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.TimeUnit;

public class GhostAbility extends ItemStackAbility {

   ItemStack item;
    public GhostAbility(ConfigurationNode configuration) {
        super("ghost",configuration);
        this.item = ItemStackBuilder.of(Material.BEACON).build();
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
        LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());
        if (!cooldown.test(localPlayer)){
            localPlayer.msg("&e(Sonic) &cPlease wait, {0} second(s) left", cooldown.remainingTime(localPlayer, TimeUnit.SECONDS));
            return;
        }
        //localPlayer.toBukkit().setInvisible();
        for (Player player : Bukkit.getOnlinePlayers()){
            player.hidePlayer(KitPlugin.getSingleton(),localPlayer.toBukkit());
        }
        Bukkit.getScheduler().runTaskLater(KitPlugin.getSingleton(), new Runnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()){
                    player.showPlayer(KitPlugin.getSingleton(),localPlayer.toBukkit());
                }
            }
        },300L);


    }

    @Override
    public void whenLeftClicked(PlayerInteractEvent e) {

    }

    @Override
    public void handleListeners() {

    }
}

/*
Ghost: Leather cap:
Unbreaking III, Protection III. Iron body, Iron legs, Iron boots. All unbreaking III. Diamond sword: Unbreaking III. 1 Beacon. 34 soup. (
 When right clicking the beacon the user goes invisible for 15 seconds. Cooldown 60 Seconds.)
 */