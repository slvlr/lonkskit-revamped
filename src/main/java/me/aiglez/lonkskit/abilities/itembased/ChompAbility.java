package me.aiglez.lonkskit.abilities.itembased;

import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.lucko.helper.Events;
import me.lucko.helper.config.yaml.YAMLConfigurationLoader;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author AigleZ
 * @date 05/10/2020
 */
public class ChompAbility extends ItemStackAbility {

    public ChompAbility(YAMLConfigurationLoader configurationLoader) throws IOException {
        super("chomp", configurationLoader);
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

                    if(!cooldown.test(damager)){
                        damager.msg("&b[LonksKit] &cPlease wait, {0} second(s) left", cooldown.remainingTime(damager, TimeUnit.SECONDS));
                        return;
                    }

                    applyEffects(damager);

                    e.setDamage(configuration.getNode("damage").getDouble(5));
                    damager.msg(configuration.getNode("messages", "chomped"), victim.getLastKnownName());
                });
    }
}
