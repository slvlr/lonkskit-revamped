package me.aiglez.lonkskit.abilities.itembased;

import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.lucko.helper.Events;
import me.lucko.helper.config.ConfigurationNode;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * @author AigleZ
 * @date 05/10/2020
 */
public class ChompAbility extends ItemStackAbility {
    private final double damage;

    public ChompAbility(ConfigurationNode configuration) {
        super("chomp", configuration);
        this.damage = configuration.getNode("damage").getDouble(5);
    }

    @Override
    public void whenRightClicked(PlayerInteractEvent e) {
        e.setCancelled(true);
    }

    @Override
    public void whenLeftClicked(PlayerInteractEvent e) {
        e.setCancelled(true);
    }

    @Override
    public void registerListeners() {
        Events.subscribe(EntityDamageByEntityEvent.class)
                .filter(e -> e.getDamager() instanceof Player && e.getEntity() instanceof Player)
                .filter(AbilityPredicates.damagerHasAbility(this))
                .filter(e -> isItemStack(((Player) e.getDamager()).getInventory().getItemInMainHand()))
                .handler(e -> {
                    final LocalPlayer damager = LocalPlayer.get((Player) e.getDamager());
                    final LocalPlayer victim = LocalPlayer.get((Player) e.getEntity());

                    applyEffects(damager);

                    e.setDamage(damage);
                    damager.msg("(Debug - Chomp) &cYou have chomped {0} [damage: {1}]", victim.getLastKnownName(), damage);
                });
    }
}
