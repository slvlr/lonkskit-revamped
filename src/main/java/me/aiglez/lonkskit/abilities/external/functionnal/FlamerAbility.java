package me.aiglez.lonkskit.abilities.external.functionnal;


import me.aiglez.lonkskit.KitPlugin;
import me.aiglez.lonkskit.abilities.Ability;
import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.FunctionalAbility;
import me.lucko.helper.Events;
import me.lucko.helper.config.ConfigurationNode;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;


public class FlamerAbility extends FunctionalAbility {

    public FlamerAbility(ConfigurationNode configuration) {
        super("flamer", configuration);
    }

    @Override
    public void handleListeners() {
        Events.subscribe(EntityDamageEvent.class)
                .filter(AbilityPredicates.humanHasAbility(this))
                .handler(e -> {
                   if (e.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK
                           || e.getCause() == EntityDamageEvent.DamageCause.FIRE
                   || e.getCause() == EntityDamageEvent.DamageCause.LAVA){
                       e.setCancelled(true);
                       Player player = (Player) e.getEntity();
                       if (!player.hasPotionEffect(PotionType.STRENGTH.getEffectType())){
                           player.addPotionEffect(new PotionEffect(PotionType.STRENGTH.getEffectType(),200,1));

                       }
                   }

                });
    }
}
