package me.aiglez.lonkskit.abilities.external.itembased;

import me.aiglez.lonkskit.KitPlugin;
import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.items.ItemStackBuilder;
import me.lucko.helper.config.ConfigurationNode;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class GhostAbility extends ItemStackAbility {

   ItemStack item;
    public GhostAbility(ConfigurationNode configuration) {
        super("ghost",configuration);
        this.item = ItemStackBuilder.of(Material.BEACON)
                .name(Objects.requireNonNull(getConfiguration().getNode("name").getString()))
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
        LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());
        ItemStack[] armor = localPlayer.toBukkit().getInventory().getArmorContents();
        if (!cooldown.test(localPlayer)){
            localPlayer.msg("&e(Ghost) &cPlease wait, {0} second(s) left", cooldown.remainingTime(localPlayer, TimeUnit.SECONDS));
            return;
        }
        int duration = getConfiguration().getNode("duration").getInt();
        int level = getConfiguration().getNode("level").getInt();
        if (isItemStack(e.getPlayer().getInventory().getItemInMainHand())){
            if (!localPlayer.toBukkit().hasPotionEffect(PotionEffectType.INVISIBILITY)){
                clearArmor(localPlayer.toBukkit());
                localPlayer.toBukkit().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,duration * 20,level,true,false));
                localPlayer.toBukkit().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,duration * 20,level,true,false));

            }
            Bukkit.getScheduler().runTaskLater(KitPlugin.getSingleton(), () -> localPlayer.toBukkit().getInventory().setArmorContents(armor),300L);
        }


    }

    @Override
    public void whenLeftClicked(PlayerInteractEvent e) {

    }

    @Override
    public void handleListeners() {

    }
    public static void clearArmor(Player player){
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
    }
}

/*
Ghost: Leather cap:
Unbreaking III, Protection III. Iron body, Iron legs, Iron boots. All unbreaking III. Diamond sword: Unbreaking III. 1 Beacon. 34 soup. (
 When right clicking the beacon the user goes invisible for 15 seconds. Cooldown 60 Seconds.)
 */