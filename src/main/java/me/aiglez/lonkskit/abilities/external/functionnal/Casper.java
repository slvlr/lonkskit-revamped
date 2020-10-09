package me.aiglez.lonkskit.abilities.external.functionnal;

import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.FunctionalAbility;
import me.aiglez.lonkskit.abilities.external.itembased.ElderAbility;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
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
                    int a = 0;
                    if (player.isSneaking()){
                        if (a == 0) {
                            player.setInvisible(true);
                            player.getInventory().setArmorContents(null);
                            player.teleport(player.getLocation().add(10, 0, 0));
                            a = 4;
                        }
                    }
                    if (!player.isSneaking()){
                        a = 0;
                        if (a == 4){
                            player.setInvisible(false);
                            player.getInventory().setArmorContents(armor);
                        }

                    }

                });
        Events.subscribe(PlayerMoveEvent.class)
                .filter(AbilityPredicates.playerHasAbility(this))
                .filter(e -> e.getPlayer().hasPotionEffect(PotionEffectType.INVISIBILITY))
                .handler(e -> e.setCancelled(true));
        Events.subscribe(EntityDamageByEntityEvent.class)
                .filter(e -> AbilityPredicates.HavetheKit(this,e))
                .filter(e -> e.getDamager() instanceof Player && e.getEntity() instanceof Player)
                .filter(e -> ((Player)e.getDamager()).hasPotionEffect(PotionEffectType.INVISIBILITY))
                .handler(e -> e.setCancelled(true));
    }
}
/*When shifting casper turns completely invisible, his armor will be removed while he is invisible.
Make his hand slot also invisible if possible. When shifting casper will not be able to move or deal damage to anyone.
 Although casper will be able to take damage if hit.
When Casper crouches it will teleport them up to 4 blocks away from their current location,
 and when they stop crouching that is when they will come visible.
*/
/**/