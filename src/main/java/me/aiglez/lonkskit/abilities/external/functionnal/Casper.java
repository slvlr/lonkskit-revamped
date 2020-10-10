package me.aiglez.lonkskit.abilities.external.functionnal;

import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.FunctionalAbility;
import me.aiglez.lonkskit.abilities.external.itembased.ElderAbility;
import me.aiglez.lonkskit.abilities.external.itembased.GhostAbility;
import me.lucko.helper.Events;
import me.lucko.helper.config.ConfigurationNode;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Casper extends FunctionalAbility {
    public Casper(ConfigurationNode configuration) {
        super("casper", configuration);
    }

    @Override
    public void handleListeners() {
        Events.subscribe(PlayerToggleSneakEvent.class)
                .filter(AbilityPredicates.playerHasAbility(this))
                .handler(e->{
                    Player player = e.getPlayer();
                    ItemStack item = player.getInventory().getItemInMainHand();
                    ItemStack[] armor = player.getInventory().getArmorContents();
                    if (player.isSneaking()){
                        player.sendMessage("Sneaking");
                        if (!player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 423, true, false, true));
                            GhostAbility.clearArmor(player);
                            player.teleport(player.getLocation().add(10, 0, 0));
                        }

                    }
                    if (!player.isSneaking()){
                        player.sendMessage("DISABLE SNEAKING");
                            if (player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                                player.removePotionEffect(PotionEffectType.INVISIBILITY);
                                player.getInventory().setArmorContents(armor);
                            }

                    }

                });
        Events.subscribe(PlayerMoveEvent.class)
                .filter(AbilityPredicates.playerHasAbility(this))
                .filter(e -> e.getPlayer().hasPotionEffect(PotionEffectType.INVISIBILITY) && e.getPlayer().isSneaking())
                .handler(e -> e.setCancelled(true));
        Events.subscribe(EntityDamageByEntityEvent.class)
                .filter(e -> AbilityPredicates.HavetheKit(this,e))
                .filter(e -> e.getDamager() instanceof Player && e.getEntity() instanceof Player)
                .filter(e -> ((Player)e.getDamager()).hasPotionEffect(PotionEffectType.INVISIBILITY))
                .handler(e -> e.setCancelled(true));
    }
}
/*
*/
/**/