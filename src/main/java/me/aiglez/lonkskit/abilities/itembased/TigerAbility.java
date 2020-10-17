package me.aiglez.lonkskit.abilities.itembased;

import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.MetadataProvider;
import me.aiglez.lonkskit.utils.items.ItemStackBuilder;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.config.ConfigurationNode;
import me.lucko.helper.metadata.ExpiringValue;
import me.lucko.helper.scheduler.Ticks;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.concurrent.TimeUnit;

public class TigerAbility extends ItemStackAbility {

    public TigerAbility(ConfigurationNode configuration) {
        super("tiger", configuration);
        this.item = ItemStackBuilder.of(Material.GHAST_TEAR)
                .name(configuration.getNode("item-name").getString("Tiger"))
                .build();
    }

    public void whenRightClicked(PlayerInteractEvent e) {
        e.setCancelled(true);
        LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());
        if (!this.cooldown.test(localPlayer)) {
            localPlayer.msg("&b[LonksKit] &cPlease wait, {0} second(s) left", this.cooldown.remainingTime(localPlayer, TimeUnit.SECONDS));
            return;
        }

        applyEffects(localPlayer);

        localPlayer.metadata().put(MetadataProvider.PLAYER_DOUBLE_DAMAGE, ExpiringValue.of(true, 10L, TimeUnit.SECONDS));
        localPlayer.toBukkit().chat(configuration.getNode("messages", "start").getString("Message Null: start"));
        Schedulers.async()
                .runLater(() -> localPlayer.metadata().remove(MetadataProvider.PLAYER_DOUBLE_DAMAGE),
                        Ticks.from(configuration.getNode("duration").getLong(10L), TimeUnit.SECONDS))
                .thenRunSync(() -> localPlayer.msg(configuration.getNode("messages", "end").getString("Message Null: end")));
    }

    public void whenLeftClicked(PlayerInteractEvent e) {}

    @Override
    public void registerListeners() {
        Events.subscribe(EntityDamageByEntityEvent.class)
                .filter(AbilityPredicates.damagerHasAbility(this))
                .handler(e -> {
                    LocalPlayer damager = LocalPlayer.get((Player)e.getDamager());
                    if (damager.metadata().has(MetadataProvider.PLAYER_DOUBLE_DAMAGE)) {
                        e.setDamage(e.getDamage() * configuration.getNode("multiply").getDouble(1D));
                    }
        });
    }
}
