package me.aiglez.lonkskit.abilities.external.itembased;

import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.items.ItemStackBuilder;
import me.lucko.helper.config.ConfigurationNode;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class HyperAbility extends ItemStackAbility {
    ItemStack item;

    public HyperAbility(ConfigurationNode configuration) {
        super("hyper", configuration);
        this.item = ItemStackBuilder.of(Material.SUGAR)
                .name(Objects.requireNonNull(getConfiguration().getNode("name").getString()))
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
    public void whenRightClicked(PlayerInteractEvent e) {
        e.setCancelled(true);
        final LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());
        if (!cooldown.test(localPlayer)){
            localPlayer.msg("&e(Sonic) &cPlease wait, {0} second(s) left", cooldown.remainingTime(localPlayer, TimeUnit.SECONDS));
            return;
        }
        int duration = getConfiguration().getNode("duration").getInt() * 20;
        int level = getConfiguration().getNode("level").getInt();
        int level1 = getConfiguration().getNode("level2").getInt();
        if (!(localPlayer.toBukkit().hasPotionEffect(PotionEffectType.SPEED) &&
                localPlayer.toBukkit().hasPotionEffect(PotionEffectType.REGENERATION) &&
                localPlayer.toBukkit().hasPotionEffect(Objects.requireNonNull(PotionType.STRENGTH.getEffectType())))){
            localPlayer.toBukkit().addPotionEffect(new PotionEffect(PotionEffectType.SPEED,duration,level));
            localPlayer.toBukkit().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,duration,level1));
            assert PotionType.STRENGTH.getEffectType() != null;
            localPlayer.toBukkit().addPotionEffect(new PotionEffect(PotionType.STRENGTH.getEffectType(),260,1));
        }else
            localPlayer.msg("&e You have Already the Potion Effects");
    }

    @Override
    public void whenLeftClicked(PlayerInteractEvent e) {

    }

    @Override
    public void handleListeners() {

    }
}
