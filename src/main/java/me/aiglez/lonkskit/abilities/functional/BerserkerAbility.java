package me.aiglez.lonkskit.abilities.functional;

import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.lucko.helper.Events;
import me.lucko.helper.config.yaml.YAMLConfigurationLoader;
import org.bukkit.entity.Fireball;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.IOException;

public class BerserkerAbility extends ItemStackAbility {

    public BerserkerAbility(YAMLConfigurationLoader configurationLoader) throws IOException {
        super("berserker", configurationLoader);
    }

    @Override
    public void registerListeners() {
        Events.subscribe(PlayerDeathEvent.class)
                .filter(e -> e.getEntity().getKiller() != null)
                .filter(AbilityPredicates.isKillerhaveAbility(this))
                .handler(e -> applyEffects(LocalPlayer.get(e.getEntity().getKiller())));

    }

    @Override
    public void whenRightClicked(PlayerInteractEvent e) {
        e.setCancelled(true);
        final LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());
        applyEffects(localPlayer);
        final Fireball fireball = localPlayer.toBukkit().launchProjectile(Fireball.class);
        fireball.setShooter(localPlayer.toBukkit());
    }

    @Override
    public void whenLeftClicked(PlayerInteractEvent e) {

    }
}
