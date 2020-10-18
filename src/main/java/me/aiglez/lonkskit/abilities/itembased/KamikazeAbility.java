package me.aiglez.lonkskit.abilities.itembased;

import me.aiglez.lonkskit.WorldProvider;
import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.Various;
import me.lucko.helper.config.ConfigurationNode;
import org.bukkit.event.player.PlayerInteractEvent;

public class KamikazeAbility extends ItemStackAbility {

    public KamikazeAbility(ConfigurationNode configuration) {
        super("kamikaze", configuration);
    }

    @Override
    public void whenRightClicked(PlayerInteractEvent e) {
        e.setCancelled(true);
        final LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());

        applyEffects(localPlayer);

        WorldProvider.KP_WORLD.createExplosion(
                localPlayer.getLocation(),
                0F,
                false,
                false,
                localPlayer.toBukkit()
        );
        final double damage = localPlayer.toBukkit().getHealth();
        localPlayer.msg("&4(Kamikaze - Debug) &cYou have exploded! (HP left: {0})", damage);

        // damage
        localPlayer.toBukkit().damage(999);
        WorldProvider.KP_WORLD.getNearbyPlayers(localPlayer.getLocation(), 5D, 5D, 5D)
                .forEach(player -> {
                    Various.damage(LocalPlayer.get(player), damage, true);
                });
        // damage

    }

    @Override
    public void whenLeftClicked(PlayerInteractEvent e) {
        e.setCancelled(true);
    }

    @Override
    public void registerListeners() { }
}
