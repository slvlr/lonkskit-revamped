package me.aiglez.lonkskit.abilities.itembased;

import me.aiglez.lonkskit.WorldProvider;
import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.MetadataProvider;
import me.lucko.helper.Events;
import me.lucko.helper.config.yaml.YAMLConfigurationLoader;
import me.lucko.helper.metadata.Metadata;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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

//            WorldProvider.KP_WORLD.createExplosion(
//                    localPlayer.getLocation(),
//                    0F,
//                    false,
//                    false,
//                    localPlayer.toBukkit()
//            );
        TNTPrimed tntPrimed = WorldProvider.KP_WORLD.spawn(localPlayer.getLocation(), TNTPrimed.class,tnt -> {
            tnt.setFuseTicks(2);
            tnt.setYield(4);
            Metadata.provideForEntity(tnt).put(MetadataProvider.KAMIKAZ,true);
        });

        final double damage = Math.max(localPlayer.toBukkit().getHealth(), 0.5D);
        localPlayer.msg("&4(Kamikaze - Debug) &cYou have exploded! (HP left: {0})", damage);
        localPlayer.toBukkit().setHealth(0D);
        // damage
        WorldProvider.KP_WORLD.getNearbyPlayers(localPlayer.getLocation(),
                configuration.getNode("radius", "x-axis").getDouble(5D),
                configuration.getNode("radius", "y-axis").getDouble(5D),
                configuration.getNode("radius", "z-axis").getDouble(5D))
                .stream().filter(player -> !player.getUniqueId().equals(localPlayer.getUniqueId()))
                .forEach(player -> player.setHealth(player.getHealth() - damage < 0.5 ? 0 :  player.getHealth() - damage ));


        // damage

        localPlayer.msg(configuration.getNode("messages", "exploded"));
    }

    @Override
    public void whenLeftClicked(PlayerInteractEvent e) {
        e.setCancelled(true);
    }

    @Override
    public void registerListeners() {
        Events.subscribe(EntityDamageByEntityEvent.class)
                .filter(e -> e.getDamager() instanceof TNTPrimed)
                .filter(e -> e.getEntity() instanceof Player)
                .filter(e -> Metadata.getForEntity(e.getDamager()).get().has(MetadataProvider.KAMIKAZ))
                .handler(e -> {
                    e.setCancelled(true);
                    e.getEntity().sendMessage("WELL");
                });
    }
}
