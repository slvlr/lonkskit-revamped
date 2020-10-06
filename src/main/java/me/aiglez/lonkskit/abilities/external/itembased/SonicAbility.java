package me.aiglez.lonkskit.abilities.external.itembased;

import me.aiglez.lonkskit.KitPlugin;
import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.items.ItemStackBuilder;
import me.lucko.helper.Schedulers;
import me.lucko.helper.config.ConfigurationNode;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;


import java.util.concurrent.TimeUnit;

public class SonicAbility extends ItemStackAbility {
    ItemStack item;

    public SonicAbility(ConfigurationNode configuration) {
        super("sonic", configuration);
        this.item = ItemStackBuilder.of(Material.SUGAR).build();
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
        e.setCancelled(true);
        final LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());
        if (!cooldown.test(localPlayer)){
            localPlayer.msg("&e(Sonic) &cPlease wait, {0} second(s) left", cooldown.remainingTime(localPlayer, TimeUnit.SECONDS));
            return;
        }
        if (isItemStack(localPlayer.toBukkit().getInventory().getItemInMainHand())) {
            if (getConfiguration().getValue("duration") != null) {
                PotionEffect speed3 = new PotionEffect(PotionEffectType.SPEED, Integer.parseInt((String) getConfiguration().getValue("duration")) * 20, 3);
                if (!localPlayer.toBukkit().hasPotionEffect(PotionEffectType.SPEED)) {
                    localPlayer.toBukkit().addPotionEffect(speed3);
                } else
                    localPlayer.msg("&c You have Speed III Already !!");
            }else if (getConfiguration().getValue("duration") == null){
                PotionEffect speed3 = new PotionEffect(PotionEffectType.SPEED, 300, 3);
                if (!localPlayer.toBukkit().hasPotionEffect(PotionEffectType.SPEED)) {
                    localPlayer.toBukkit().addPotionEffect(speed3);
                } else
                    localPlayer.msg("&c You have Speed III Already !!");
            }
        }
    }

    @Override
    public void whenLeftClicked(PlayerInteractEvent e) {

    }

    @Override
    public void handleListeners() {

    }
}
/*
Sonic: Leather Helmet and leather legs: DYED BLUE
Unbreaking III, Protection II. Iron body, Iron legs: Unbreaking III. Diamond sword: Unbreaking III. 1 Sugar. 34 soup.
(When right clicking the sugar, the player gets Speed III for 15 seconds with a 30 second cooldown.)*/