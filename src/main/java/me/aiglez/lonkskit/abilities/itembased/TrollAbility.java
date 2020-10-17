package me.aiglez.lonkskit.abilities.itembased;

import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.items.ItemStackBuilder;
import me.lucko.helper.Events;
import me.lucko.helper.config.ConfigurationNode;
import me.lucko.helper.random.RandomSelector;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;

/**
 * @author AigleZ
 * @date 03/10/2020
 */
public class TrollAbility extends ItemStackAbility {

    private final RandomSelector<PotionEffect> potionEffectRandomSelector;

    public TrollAbility(ConfigurationNode configuration) {
        super("troll", configuration);
        this.item = ItemStackBuilder.of(Material.WOODEN_HOE)
                .name(configuration.getNode("item-name").getString("Trolololololo"))
                .enchant(Enchantment.DURABILITY, 1)
                .build();

        try {
            this.potionEffectRandomSelector = RandomSelector.uniform(potionEffects);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Troll Ability: You must specify potion effects.");
        }

    }

    // --------------------------------------------------------------------------------------------
    @Override
    public void whenRightClicked(PlayerInteractEvent e) { }

    @Override
    public void whenLeftClicked(PlayerInteractEvent e) { }

    @Override
    public void registerListeners() {
        Events.subscribe(EntityDamageByEntityEvent.class)
                .filter(e -> e.getDamager() instanceof Player && e.getEntity() instanceof Player)
                .filter(AbilityPredicates.damagerHasAbility(this))
                .filter(e -> isItemStack(((Player) e.getDamager()).getInventory().getItemInMainHand()))
                .handler(e -> {
                    LocalPlayer damager = LocalPlayer.get((Player) e.getDamager());
                    LocalPlayer victim = LocalPlayer.get((Player) e.getEntity());

                    victim.toBukkit().addPotionEffect(potionEffectRandomSelector.pick());

                    damager.msg(configuration.getNode("messages", "trolled").getString("Message trolled Null"), victim.getLastKnownName());
                });
    }
}
