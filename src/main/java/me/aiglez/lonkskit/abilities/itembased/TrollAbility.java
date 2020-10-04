package me.aiglez.lonkskit.abilities.itembased;

import com.google.common.collect.ImmutableList;
import me.aiglez.lonkskit.abilities.Ability;
import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.items.ItemStackBuilder;
import me.lucko.helper.config.ConfigurationNode;
import me.lucko.helper.random.RandomSelector;
import me.lucko.helper.scheduler.Ticks;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.TimeUnit;

public class TrollAbility extends ItemStackAbility {

    private final ItemStack item;
    private final RandomSelector<PotionEffectType> potionEffectTypeRandomSelector = RandomSelector.uniform(ImmutableList.of(
            PotionEffectType.POISON, PotionEffectType.BLINDNESS, PotionEffectType.SLOW_DIGGING
    ));

    public TrollAbility(ConfigurationNode configuration) {
        super("troll", configuration);
        this.item = ItemStackBuilder.of(Material.WOODEN_HOE)
                .name("&dtrolololol")
                .enchant(Enchantment.DURABILITY, 1)
                .build();
    }

    @Override
    public ItemStack getItemStack() { return this.item; }

    @Override
    public boolean isItemStack(ItemStack item) { return this.item.isSimilar(item); }

    // --------------------------------------------------------------------------------------------
    @Override
    public void whenUsed(PlayerInteractEvent e) {

    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        if(!(e.getDamager() instanceof Player) || !(e.getEntity() instanceof Player)) return;
        LocalPlayer victim = LocalPlayer.get((Player) e.getEntity());
        LocalPlayer damager = LocalPlayer.get((Player) e.getDamager());

        if(damager.hasSelectedKit() && damager.getNullableSelectedKit().hasAbility(Ability.get("troll"))) {
            ItemStack item = damager.toBukkit().getInventory().getItemInMainHand();
            if(isItemStack(item)) {
                victim.toBukkit().addPotionEffect(new PotionEffect(
                        potionEffectTypeRandomSelector.pick(), (int) Ticks.from(4, TimeUnit.SECONDS), 1
                ));
                damager.msg("&cYou have trolled {0}", victim.getLastKnownName());
            }
        }
    }

}
