package me.aiglez.lonkskit.abilities.itembased;

import me.aiglez.lonkskit.WorldProvider;
import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.Various;
import me.lucko.helper.config.yaml.YAMLConfigurationLoader;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class KamikazeAbility extends ItemStackAbility {

    public KamikazeAbility(YAMLConfigurationLoader configurationLoader) throws IOException {
        super("kamikaze", configurationLoader);
    }

    @Override
    public void whenRightClicked(PlayerInteractEvent e) {
        e.setCancelled(true);
        final LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());

        if(!cooldown.test(localPlayer)){
            localPlayer.msg("&b[LonksKit] &cPlease wait, {0} second(s) left", cooldown.remainingTime(localPlayer, TimeUnit.SECONDS));
            return;
        }

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
        localPlayer.toBukkit().setHealth(0D);
        WorldProvider.KP_WORLD.getNearbyPlayers(localPlayer.getLocation(),
                configuration.getNode("radius", "x-axis").getDouble(5D),
                configuration.getNode("radius", "y-axis").getDouble(5D),
                configuration.getNode("radius", "z-axis").getDouble(5D))
                .stream().filter(player -> !player.getUniqueId().equals(localPlayer.getUniqueId()))
                .forEach(player -> {
                    Various.damage(LocalPlayer.get(player), damage, true);
                });
        // damage

        localPlayer.msg(configuration.getNode("messages", "exploded"));
    }

    @Override
    public void whenLeftClicked(PlayerInteractEvent e) {
        e.setCancelled(true);
    }

    @Override
    public void registerListeners() { }
}
