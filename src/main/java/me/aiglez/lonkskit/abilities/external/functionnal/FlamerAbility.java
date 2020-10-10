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
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;


public class FlamerAbility extends FunctionalAbility {

    public FlamerAbility(ConfigurationNode configuration) {
        super("flamer", configuration);
    }

    @Override
    public void handleListeners() {
        Events.subscribe(PlayerMoveEvent.class)
                .filter(AbilityPredicates.playerHasAbility(this))
                .handler(e -> {
                    boolean check = e.getPlayer().getLocation().getBlock().getType() == Material.LAVA || e.getPlayer().getLocation().getBlock().getType() == Material.FIRE
                            || e.getPlayer().getLocation().getBlock().getType() == Material.MAGMA_BLOCK || e.getPlayer().getLocation().getBlock().getType() == Material.LAVA || e.getPlayer().getLocation().getBlock().getType() == Material.FIRE
                            || e.getPlayer().getLocation().getBlock().getType() == Material.LEGACY_STATIONARY_LAVA;
                        if (e.getPlayer().getLocation().getBlock().isLiquid()) {
                            if (check){
                                if (!e.getPlayer().hasPotionEffect(PotionType.STRENGTH.getEffectType())){
                                    e.getPlayer().addPotionEffect(new PotionEffect(PotionType.STRENGTH.getEffectType(),Integer.MAX_VALUE,1));
                            }
                        }
                    }
                    if (!check){
                        if (e.getPlayer().hasPotionEffect(PotionType.STRENGTH.getEffectType())){
                            e.getPlayer().removePotionEffect(PotionType.STRENGTH.getEffectType());
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