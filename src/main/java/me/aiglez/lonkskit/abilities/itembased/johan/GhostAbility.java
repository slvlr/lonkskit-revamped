package me.aiglez.lonkskit.abilities.itembased.johan;

import me.aiglez.lonkskit.KitPlugin;
import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.lucko.helper.config.yaml.YAMLConfigurationLoader;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class GhostAbility extends ItemStackAbility {

    public GhostAbility(YAMLConfigurationLoader configurationLoader) throws IOException {
        super("ghost", configurationLoader);
    }

    @Override
    public void whenRightClicked(PlayerInteractEvent e) {
        LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());
        ItemStack[] armor = localPlayer.toBukkit().getInventory().getArmorContents();
        if (!cooldown.test(localPlayer)){
            localPlayer.msg("&b[LonksKit] &cPlease wait, {0} second(s) left", cooldown.remainingTime(localPlayer, TimeUnit.SECONDS));
            return;
        }
        if (isItemStack(e.getPlayer().getInventory().getItemInMainHand())){
            clearArmor(localPlayer.toBukkit());
            applyEffects(localPlayer);

            Bukkit.getScheduler().runTaskLater(KitPlugin.getSingleton(), () -> localPlayer.toBukkit().getInventory().setArmorContents(armor),300L);
        }
    }

    @Override
    public void whenLeftClicked(PlayerInteractEvent e) { }

    @Override
    public void registerListeners() {

    }

    public static void clearArmor(Player player){
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
    }
}
