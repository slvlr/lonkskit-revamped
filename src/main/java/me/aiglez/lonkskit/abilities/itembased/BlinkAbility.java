package me.aiglez.lonkskit.abilities.itembased;

import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.items.ItemStackBuilder;
import me.lucko.helper.config.ConfigurationNode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.concurrent.TimeUnit;

/**
 * @author AigleZ
 * @date 03/10/2020 at 22:33
 */
public class BlinkAbility extends ItemStackAbility {

    public BlinkAbility(ConfigurationNode configuration) {
        super("blink", configuration);
        this.item = ItemStackBuilder.of(Material.REDSTONE_TORCH)
                .name(configuration.getNode("item-name").getString("Blinker"))
                .build();
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

        int range = configuration.getNode("range").getInt(50);
        Location from = localPlayer.toBukkit().getEyeLocation();
        Location to = localPlayer.toBukkit().getEyeLocation();

        // TODO: safe zone check
        while (to.distance(localPlayer.toBukkit().getEyeLocation()) <= range) {
            from = to;
            to = from.add(localPlayer.getLocation().getDirection().normalize().multiply(.5));
            if (isValid(to.getBlock().getType())) {
                localPlayer.toBukkit().teleport(from.clone().add(0, 1, 0));
                localPlayer.msg(configuration.getNode("messages", "blinked").getString("Blinked Message Null"));
                return;
            }
        }
        localPlayer.toBukkit().teleport(to.add(0, 1, 0));
        localPlayer.msg(configuration.getNode("messages", "blinked").getString("Blinked Message Null"));
    }

    @Override
    public void whenLeftClicked(PlayerInteractEvent e) {
        e.setCancelled(true);
    }

    @Override
    public void registerListeners() { }

    private boolean isValid(Material material) {
        return !material.isAir() && material != Material.GRASS && material != Material.TALL_GRASS;
    }
}
