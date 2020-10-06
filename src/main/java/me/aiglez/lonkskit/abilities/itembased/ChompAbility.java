package me.aiglez.lonkskit.abilities.itembased;

import com.google.common.collect.Sets;
import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.items.ItemStackBuilder;
import me.lucko.helper.Events;
import me.lucko.helper.config.ConfigurationNode;
import me.lucko.helper.scheduler.Ticks;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author AigleZ
 * @date 05/10/2020
 */
public class ChompAbility extends ItemStackAbility {

    private final ItemStack item;
    private final double damage;
    private final Set<PotionEffect> negativeEffects;

    public ChompAbility(ConfigurationNode configuration) {
        super("chomp", configuration);
        this.item = ItemStackBuilder.of(Material.CHEST)
                .name("&6Chomp")
                .build();
        this.damage = configuration.getNode("damage").getDouble(5);
        this.negativeEffects = Sets.newHashSet(
                new PotionEffect(PotionEffectType.SLOW, (int) Ticks.from(6, TimeUnit.SECONDS), 1),
                new PotionEffect(PotionEffectType.WEAKNESS, (int) Ticks.from(6, TimeUnit.SECONDS), 1)
        );
    }

    @Override
    public ItemStack getItemStack() { return this.item; }

    @Override
    public boolean isItemStack(ItemStack item) { return this.item.isSimilar(item); }

    @Override
    public void whenRightClicked(PlayerInteractEvent e) { }

    @Override
    public void whenLeftClicked(PlayerInteractEvent e) { }

    @Override
    public void handleListeners() {
        Events.subscribe(EntityDamageByEntityEvent.class)
                .filter(e -> e.getDamager() instanceof Player)
                .filter(e -> {
                    final LocalPlayer localPlayer = LocalPlayer.get((Player) e.getDamager());
                    return localPlayer.hasSelectedKit() && localPlayer.getNullableSelectedKit().hasAbility(this);
                })
                .filter(e ->{
                    return isItemStack(((Player) e.getDamager()).getInventory().getItemInMainHand());
                })
                .handler(e -> {
                    LocalPlayer damager = LocalPlayer.get((Player) e.getDamager());
                    LivingEntity victim = (LivingEntity) e.getEntity();

                    double oldHP = victim.getHealth();

                    //damager.toBukkit().addPotionEffects(negativeEffects);

                    e.setDamage(damage);
                    damager.msg("(Chomp) &cYou have chomped {0} [damage: {1}] (OLD HP: {2}  NEW HP: {3})", victim.getName(), damage,
                            oldHP, victim.getHealth());
                });
    }
}
