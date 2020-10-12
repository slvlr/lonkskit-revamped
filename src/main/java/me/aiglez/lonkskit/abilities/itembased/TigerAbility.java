package me.aiglez.lonkskit.abilities.itembased;

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
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.TimeUnit;

public class TigerAbility extends ItemStackAbility {

    private final ItemStack item;

    public TigerAbility(ConfigurationNode configuration) {
        super("tiger", configuration);
        this.item = ItemStackBuilder.of(Material.GHAST_TEAR)
                .name(configuration.getNode("item-name").getString("Tiger"))
                .build();
    }

    public ItemStack getItemStack() { return this.item; }

    public boolean isItemStack(ItemStack item) { return this.item.isSimilar(item); }

    public void whenRightClicked(PlayerInteractEvent e) {
        e.setCancelled(true);
        LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());
        if (!this.cooldown.test(localPlayer)) {
            localPlayer.msg("&b[LonksKit] &cPlease wait, {0} second(s) left", this.cooldown.remainingTime(localPlayer, TimeUnit.SECONDS));
            return;
        }
        localPlayer.metadata().put(MetadataProvider.PLAYER_DOUBLE_DAMAGE, ExpiringValue.of(true, 10L, TimeUnit.SECONDS));
        localPlayer.toBukkit().chat(configuration.getNode("messages", "start").getString("Message Null: start"));
        Schedulers.async()
                .runLater(() -> localPlayer.metadata().remove(MetadataProvider.PLAYER_DOUBLE_DAMAGE),
                        Ticks.from(configuration.getNode("duration").getLong(10L), TimeUnit.SECONDS))
                .thenRunSync(() -> localPlayer.msg(configuration.getNode("messages", "end").getString("Message Null: end")));
    }

    public void whenLeftClicked(PlayerInteractEvent e) {}

    public void handleListeners() {
        Events.subscribe(EntityDamageByEntityEvent.class)
                .filter(e -> e.getDamager() instanceof Player)
                .filter(e -> {
                    LocalPlayer localPlayer = LocalPlayer.get((Player)e.getDamager());
                    return (localPlayer.hasSelectedKit() && localPlayer.getNullableSelectedKit().hasAbility(this));
                }).handler(e -> {

            LocalPlayer damager = LocalPlayer.get((Player)e.getDamager());
            if (damager.metadata().has(MetadataProvider.PLAYER_DOUBLE_DAMAGE)) {
                e.setDamage(e.getDamage() * configuration.getNode("multiply").getDouble(1D));
            }
        });
    }
}
