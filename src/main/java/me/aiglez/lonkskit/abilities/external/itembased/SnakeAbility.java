package me.aiglez.lonkskit.abilities.external.itembased;

import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.utils.items.ItemStackBuilder;
import me.lucko.helper.Events;
import me.lucko.helper.config.ConfigurationNode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SnakeAbility extends ItemStackAbility {
    ItemStack item;
    public SnakeAbility(ConfigurationNode configuration) {
        super("snake", configuration);
        this.item = ItemStackBuilder.of(Material.IRON_SWORD).build();
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


    }

    @Override
    public void whenLeftClicked(PlayerInteractEvent e) {

    }

    @Override
    public void handleListeners() {
        Events.subscribe(EntityDamageByEntityEvent.class)
                .filter(e -> e.getDamager() instanceof Player && e.getEntity() instanceof Player)
                .filter(e -> AbilityPredicates.HavetheKit(this,e))
                .handler(e -> {
                    Player player = (Player) e.getEntity();
                    player.addPotionEffect(new PotionEffect(PotionEffectType.POISON,80,2));
                });

    }
}
