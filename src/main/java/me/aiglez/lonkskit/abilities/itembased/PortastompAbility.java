package me.aiglez.lonkskit.abilities.itembased;

import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.lucko.helper.Schedulers;
import me.lucko.helper.config.yaml.YAMLConfigurationLoader;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author AigleZ
 * @date 02/10/2020
 */
public class PortastompAbility extends ItemStackAbility {

    public PortastompAbility(YAMLConfigurationLoader yamlConfigurationLoader) throws IOException {
        super("portastomp", yamlConfigurationLoader);
    }

    // --------------------------------------------------------------------------------------------
    @Override
    public void whenRightClicked(PlayerInteractEvent e) {
        e.setCancelled(true);
        final LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());

        if(!cooldown.test(localPlayer)){
            localPlayer.msg("&b[LonksKit] &cPlease wait, {0} second(s) left", cooldown.remainingTime(localPlayer, TimeUnit.SECONDS));
            return;
        }

        applyEffects(localPlayer);

        Schedulers.sync().runLater(() -> localPlayer.toBukkit().setVelocity(new Vector(0, configuration.getNode("velocities", "y-component").getDouble(1), 0)), 2L);
    }

    @Override
    public void whenLeftClicked(PlayerInteractEvent e) {
        e.setCancelled(true);
    }

    @Override
    public void registerListeners() { }
}
