package me.aiglez.lonkskit.abilities.itembased;

import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.lucko.helper.Events;
import me.lucko.helper.config.yaml.YAMLConfigurationLoader;
import me.lucko.helper.random.RandomSelector;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;

import java.io.IOException;

/**
 * @author AigleZ
 * @date 03/10/2020
 */
public class TrollAbility extends ItemStackAbility {

    private final RandomSelector<PotionEffect> potionEffectRandomSelector;

    public TrollAbility(YAMLConfigurationLoader configurationLoader) throws IOException {
        super("troll", configurationLoader);
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

                    damager.msg(configuration.getNode("messages", "trolled"), victim.getLastKnownName());
                });
    }
}
