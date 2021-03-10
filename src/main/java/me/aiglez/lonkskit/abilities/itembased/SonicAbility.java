package me.aiglez.lonkskit.abilities.itembased;

import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.lucko.helper.config.yaml.YAMLConfigurationLoader;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class SonicAbility extends ItemStackAbility {

    public SonicAbility( YAMLConfigurationLoader configurationLoader) throws IOException {
        super("sonic", configurationLoader);
    }

    @Override
    public void whenRightClicked(PlayerInteractEvent e) {
        e.setCancelled(true);
        final LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());
        if (!cooldown.test(localPlayer)){
            localPlayer.msg("&e(Sonic) &cPlease wait, {0} second(s) left", cooldown.remainingTime(localPlayer, TimeUnit.SECONDS));
            return;
        }
        if (isItemStack(localPlayer.toBukkit().getInventory().getItemInMainHand())) {
            applyEffects(localPlayer);
        }
    }

    @Override
    public void whenLeftClicked(PlayerInteractEvent e) {

    }

    @Override
    public void registerListeners() {

    }
}
