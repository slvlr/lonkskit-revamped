package me.aiglez.lonkskit.abilities.external.functionnal;


import me.aiglez.lonkskit.KitPlugin;
import me.aiglez.lonkskit.abilities.Ability;
import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.FunctionalAbility;
import me.lucko.helper.Events;
import me.lucko.helper.config.ConfigurationNode;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
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
                    Player player = (Player) e.getEntity();
                    Block loc = player.getLocation().getBlock();
                    player.sendMessage(e.getCause().toString());
                    player.sendMessage(e.getCause().name());
                    //FIRST TRY
                    if (e.getCause() == EntityDamageEvent.DamageCause.LAVA || e.getCause() == EntityDamageEvent.DamageCause.HOT_FLOOR || e.getCause() == EntityDamageEvent.DamageCause.FIRE || e.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK){
                        e.setCancelled(true);
                        if (!player.hasPotionEffect(PotionType.STRENGTH.getEffectType())) {
                            player.addPotionEffect(new PotionEffect(PotionType.STRENGTH.getEffectType(), 200, 1));
                        }
                    }
                    //SECOND ONE
                    if (e.getCause() == EntityDamageEvent.DamageCause.LAVA && e.getCause() == EntityDamageEvent.DamageCause.FIRE && e.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK){
                        e.setCancelled(true);
                        if (!player.hasPotionEffect(PotionType.STRENGTH.getEffectType())) {
                            player.addPotionEffect(new PotionEffect(PotionType.STRENGTH.getEffectType(), 200, 1));
                        }
                    }
                    if (e.getCause() == EntityDamageEvent.DamageCause.HOT_FLOOR){
                        e.setCancelled(true);
                        if (!player.hasPotionEffect(PotionType.STRENGTH.getEffectType())) {
                            player.addPotionEffect(new PotionEffect(PotionType.STRENGTH.getEffectType(), 200, 1));
                        }
                    }


                });
    }
}
/*if (e.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK
                            || e.getCause() == EntityDamageEvent.DamageCause.FIRE || e.getCause() == EntityDamageEvent.DamageCause.HOT_FLOOR
                            || block.getType() == Material.LAVA || block.getType() == Material.MAGMA_BLOCK || e.getCause() == EntityDamageEvent.DamageCause.LAVA);
                    {
                        System.out.println("JOHAN");
                        e.setCancelled(true);
                        if (!player.hasPotionEffect(PotionType.STRENGTH.getEffectType())){
                            player.addPotionEffect(new PotionEffect(PotionType.STRENGTH.getEffectType(),200,1));
                            System.out.println("RANGEWONK");
                        }
                   }
                    System.out.println("WORK");*/