package me.aiglez.lonkskit.abilities.itembased;

import com.destroystokyo.paper.event.entity.ThrownEggHatchEvent;
import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.MetadataProvider;
import me.lucko.helper.Events;
import me.lucko.helper.config.yaml.YAMLConfigurationLoader;
import me.lucko.helper.metadata.Metadata;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class FlingerAbility extends ItemStackAbility {

    public FlingerAbility(YAMLConfigurationLoader configurationLoader) throws IOException {
        super("flinger", configurationLoader);
    }

    @Override
    public void whenRightClicked(PlayerInteractEvent e) {
        e.setCancelled(true);
        final LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());
        if(!cooldown.test(localPlayer)){
            localPlayer.msg("&b[LonksKit] &cPlease wait, {0} second(s) left", cooldown.remainingTime(localPlayer, TimeUnit.SECONDS));
            return;
        }

        final Egg egg = localPlayer.toBukkit().launchProjectile(Egg.class);

        egg.setShooter(localPlayer.toBukkit());
        Metadata.provideForEntity(egg).put(MetadataProvider.EGG_FLINGER, true);
        applyEffects(localPlayer);
    }

    @Override
    public void whenLeftClicked(PlayerInteractEvent e) {
        e.setCancelled(true);
    }

    @Override
    public void registerListeners() {
        Events.subscribe(EntityDamageByEntityEvent.class)
                .filter(e -> e.getDamager() instanceof Egg)
                .filter(e -> Metadata.provideForEntity(e.getDamager()).has(MetadataProvider.EGG_FLINGER))
                .handler(e -> {
                    final Egg damager = (Egg) e.getDamager();
                    final Entity victim = e.getEntity();

                    if (damager.getShooter() == null) return;

                    final LocalPlayer shooter = LocalPlayer.get((Player) damager.getShooter());
                    if (shooter.toBukkit() == null || shooter.getUniqueId().equals(victim.getUniqueId())) return;

                    if(shooter.getLocation().distance(victim.getLocation()) > 20) {
                        e.setDamage(999);
                    } else {
                        e.setDamage(2D);
                    }
                    damager.remove();
                });

        Events.subscribe(ThrownEggHatchEvent.class)
                .filter(e -> Metadata.provideForEntity(e.getEgg()).has(MetadataProvider.EGG_FLINGER))
                .handler(e -> e.setHatching(false));
    }

}
