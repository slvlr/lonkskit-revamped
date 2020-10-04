package me.aiglez.lonkskit.abilities.external.functionnal;

import me.aiglez.lonkskit.abilities.FunctionalAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.lucko.helper.config.ConfigurationNode;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class AnvilAbility extends FunctionalAbility {

    public AnvilAbility(ConfigurationNode configuration) {
        super("anvil", configuration);
    }

    public void handleKnockback(EntityDamageByEntityEvent e, LocalPlayer victim)
    {
        e.setCancelled(true);
        double damage = e.getDamage();
        victim.toBukkit().damage(damage);
    }

    @Override
    public void handleListeners() {

    }
}
