package me.aiglez.lonkskit.abilities.itembased;

import com.google.common.collect.ImmutableList;
import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.items.ItemStackBuilder;
import me.lucko.helper.Events;
import me.lucko.helper.config.ConfigurationNode;
import me.lucko.helper.random.RandomSelector;
import me.lucko.helper.scheduler.Ticks;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.TimeUnit;

/**
 * @author AigleZ
 * @date 03/10/2020
 */
public class TrollAbility extends ItemStackAbility {

    private final ItemStack item;
    private final RandomSelector<PotionEffectType> potionEffectTypeRandomSelector = RandomSelector.uniform(ImmutableList.of(
            PotionEffectType.POISON, PotionEffectType.BLINDNESS, PotionEffectType.SLOW_DIGGING
    ));

    public TrollAbility(ConfigurationNode configuration) {
        super("troll", configuration);
        this.item = ItemStackBuilder.of(Material.WOODEN_HOE)
                .name(configuration.getNode("item-name").getString("Trolololololo"))
                .enchant(Enchantment.DURABILITY, 1)
                .build();
    }

    @Override
    public ItemStack getItemStack() { return this.item; }

    @Override
    public boolean isItemStack(ItemStack item) { return this.item.isSimilar(item); }

    // --------------------------------------------------------------------------------------------
    @Override
    public void whenRightClicked(PlayerInteractEvent e) { }

    @Override
    public void whenLeftClicked(PlayerInteractEvent e) { }

    @Override
    public void handleListeners() {
        Events.subscribe(EntityDamageByEntityEvent.class)
                .filter(e -> e.getDamager() instanceof Player && e.getEntity() instanceof Player)
                .filter(e -> {
                    final LocalPlayer localPlayer = LocalPlayer.get((Player) e.getDamager());
                    return localPlayer.hasSelectedKit() && localPlayer.getNullableSelectedKit().hasAbility(this);
                })
                .filter(e -> isItemStack(((Player) e.getDamager()).getInventory().getItemInMainHand()))
                .handler(e -> {
                    LocalPlayer damager = LocalPlayer.get((Player) e.getDamager());
                    LocalPlayer victim = LocalPlayer.get((Player) e.getEntity());

                    victim.toBukkit().addPotionEffect(new PotionEffect(
                            potionEffectTypeRandomSelector.pick(), (int) Ticks.from(4, TimeUnit.SECONDS), 1
                    ));
                    damager.msg(configuration.getNode("messages", "trolled").getString("Message trolled Null"), victim.getLastKnownName());
                });
    }
}
