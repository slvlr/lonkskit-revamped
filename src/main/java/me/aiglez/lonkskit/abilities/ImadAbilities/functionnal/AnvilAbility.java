package me.aiglez.lonkskit.abilities.ImadAbilities.functionnal;

import me.aiglez.lonkskit.abilities.FunctionalAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.lucko.helper.config.ConfigurationNode;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class AnvilAbility extends FunctionalAbility {
    protected AnvilAbility(String name, ConfigurationNode configuration) {
        super(name, configuration);
    }
    @EventHandler
    public void onPlayerAttackPlayer(EntityDamageByEntityEvent event,LocalPlayer localPlayer)
    {
        if (!event.isCancelled())
        {
            // PLAYER ATTACK PLAYER
            if ((event.getEntity() instanceof HumanEntity) && (event.getDamager() instanceof HumanEntity))
            {
                // GET VICTIM AND ATTACKER
                Player victim = (Player) event.getEntity();
                LocalPlayer attacker = LocalPlayer.get((Player) event.getDamager());

                // GET EVENT DAMAGE
                double damage = event.getDamage();

                // SEND DAMAGE TO PLAYER - BECAUSE EVENT IS CANCELING
                victim.damage(damage);

                // CANCEL EVENT - TO STOP KNOCKBACK EFFECT
                event.setCancelled(true);
            }
        }
    }

}
