package me.aiglez.lonkskit.abilities.itembased.johan;

import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.lucko.helper.Events;
import me.lucko.helper.config.yaml.YAMLConfigurationLoader;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.IOException;

public class SnakeAbility extends ItemStackAbility {

    public SnakeAbility(YAMLConfigurationLoader configurationLoader) throws IOException {
        super("snake", configurationLoader);
    }

    @Override
    public void whenRightClicked(PlayerInteractEvent e) {

    }

    @Override
    public void whenLeftClicked(PlayerInteractEvent e) {

    }

    @Override
    public void registerListeners() {
        Events.subscribe(EntityDamageByEntityEvent.class)
                .filter(e -> e.getDamager() instanceof Player && e.getEntity() instanceof Player)
                .filter(e -> AbilityPredicates.HastheKit(this,e))
                .handler(e -> {
                    Player player = (Player) e.getEntity();
                    applyEffects(LocalPlayer.get(player));
                });
    }
}
