package me.aiglez.lonkskit.abilities.external.functionnal.helpers;

import me.aiglez.lonkskit.abilities.external.functionnal.Casper;
import me.aiglez.lonkskit.events.KitSelectEvent;
import me.libraryaddict.disguise.DisguiseAPI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public class CasperListener implements Listener {
    @EventHandler
    private void onPlayerMove(PlayerMoveEvent event){
        if (event.getPlayer().hasPotionEffect(PotionEffectType.INVISIBILITY)){
            if (Casper.hiddenPlayers.contains(event.getPlayer())){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    private void onDamageEvent(EntityDamageByEntityEvent e){
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Player){
            if (((Player)e.getDamager()).hasPotionEffect(PotionEffectType.INVISIBILITY)){
                if (Objects.requireNonNull(((Player) e.getDamager()).getInventory().getHelmet()).getType() == Material.WHITE_WOOL){
                    e.setCancelled(true);
                }
            }
        }
    }


}
