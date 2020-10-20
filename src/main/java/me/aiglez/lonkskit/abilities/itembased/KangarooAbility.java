package me.aiglez.lonkskit.abilities.itembased;

import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.lucko.helper.config.yaml.YAMLConfigurationLoader;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author AigleZ
 * @date 04/10/2020
 */
public class KangarooAbility extends ItemStackAbility {

    public KangarooAbility(YAMLConfigurationLoader configurationLoader) throws IOException {
        super("kangaroo", configurationLoader);
    }

    @Override
    public void whenRightClicked(PlayerInteractEvent e) {
        e.setCancelled(true);
        final LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());

        if(!cooldown.test(localPlayer)) {
            localPlayer.msg("&b[LonksKit] &cPlease wait, {0} second(s) left", cooldown.remainingTime(localPlayer, TimeUnit.SECONDS));
            return;
        }

        applyEffects(localPlayer);

        localPlayer.toBukkit().setVelocity(localPlayer.getLocation().getDirection()
                .multiply(
                        configuration.getNode("velocities", "right-click", "multiply").getDouble(1))
                .setY(
                        configuration.getNode("velocities", "right-click", "y-component").getDouble(1)
                ));
    }

    @Override
    public void whenLeftClicked(PlayerInteractEvent e) {
        e.setCancelled(true);
        final LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());

        if(!cooldown.test(localPlayer)) {
            localPlayer.msg("&b[LonksKit] &cPlease wait, {0} second(s) left", cooldown.remainingTime(localPlayer, TimeUnit.SECONDS));
            return;
        }
        localPlayer.toBukkit().setVelocity(localPlayer.getLocation().getDirection()
                .multiply(
                        configuration.getNode("velocities", "left-click", "multiply").getDouble(1)
                )
                .setY(
                        configuration.getNode("velocities", "left-click", "y-component").getDouble(1)
                ));
    }

    @Override
    public void registerListeners() { }
}
