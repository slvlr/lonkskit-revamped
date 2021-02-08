package me.aiglez.lonkskit.abilities.itembased;

import me.aiglez.lonkskit.WorldProvider;
import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.MetadataProvider;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.config.yaml.YAMLConfigurationLoader;
import me.lucko.helper.metadata.ExpiringValue;
import me.lucko.helper.scheduler.Ticks;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class TigerAbility extends ItemStackAbility {

    public TigerAbility(YAMLConfigurationLoader configurationLoader) throws IOException {
        super("tiger", configurationLoader);
    }

    public void whenRightClicked(PlayerInteractEvent e) {
        e.setCancelled(true);
        LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());
        if (!this.cooldown.test(localPlayer)) {
            localPlayer.msg("&b[LonksKit] &cPlease wait, {0} second(s) left", this.cooldown.remainingTime(localPlayer, TimeUnit.SECONDS));
            return;
        }

        applyEffects(localPlayer);

        localPlayer.getMetadata().put(MetadataProvider.PLAYER_DOUBLE_DAMAGE, ExpiringValue.of(true, 10L, TimeUnit.SECONDS));
        for (Player pl : WorldProvider.KP_WORLD.getPlayers()) {
            pl.sendMessage(ChatColor.translateAlternateColorCodes('&',configuration.getNode("messages", "start").getString("")));
        }
        Schedulers.async()
                .runLater(() -> localPlayer.getMetadata().remove(MetadataProvider.PLAYER_DOUBLE_DAMAGE),
                        Ticks.from(configuration.getNode("duration").getLong(10L), TimeUnit.SECONDS))
                .thenRunSync(() -> localPlayer.msg(configuration.getNode("messages", "end")));
    }

    public void whenLeftClicked(PlayerInteractEvent e) {}

    @Override
    public void registerListeners() {
        Events.subscribe(EntityDamageByEntityEvent.class)
                .filter(AbilityPredicates.damagerHasAbility(this))
                .handler(e -> {
                    final LocalPlayer damager = LocalPlayer.get((Player)e.getDamager());
                    if (damager.getMetadata().has(MetadataProvider.PLAYER_DOUBLE_DAMAGE)) {
                        e.setDamage(e.getDamage() * configuration.getNode("multiply").getDouble(1D));
                    }
        });
    }
}
