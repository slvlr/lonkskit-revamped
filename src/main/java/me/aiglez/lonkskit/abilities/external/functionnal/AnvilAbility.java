package me.aiglez.lonkskit.abilities.external.functionnal;

import me.aiglez.lonkskit.abilities.FunctionalAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.lucko.helper.config.ConfigurationNode;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class AnvilAbility extends FunctionalAbility {

    public AnvilAbility(ConfigurationNode configuration) {
        super("anvil", configuration);
    }

    @EventHandler
    public void onPlayerAttackPlayer(EntityDamageByEntityEvent event,LocalPlayer localPlayer)
    {
            if ((event.getEntity() instanceof Player) && (event.getDamager() instanceof Player))
            {
                event.setCancelled(true);
                Player victim = (Player) event.getEntity();
                LocalPlayer attacker = LocalPlayer.get((Player) event.getDamager());

                double damage = event.getDamage();

                victim.damage(damage);


        }
    }

}
