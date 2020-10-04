package me.aiglez.lonkskit.abilities.itembased;

import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.MetadataProvider;
import me.aiglez.lonkskit.utils.items.ItemStackBuilder;
import me.lucko.helper.Events;
import me.lucko.helper.config.ConfigurationNode;
import me.lucko.helper.metadata.ExpiringValue;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.concurrent.TimeUnit;

/**
 * @author AigleZ
 * @date 01/10/2020 at 13:50
 */
public class DragonAbility extends ItemStackAbility {

    private final ItemStack item;
    private final double strength;

    public DragonAbility(ConfigurationNode configuration) {
        super("dragon", configuration);
        this.item = ItemStackBuilder.of(Material.FEATHER)
                .name("&bBoost")
                .build();
        this.strength = configuration.getNode("strength").getDouble(5D);
    }

    @Override
    public ItemStack getItemStack() { return this.item; }

    @Override
    public boolean isItemStack(ItemStack item) { return this.item.isSimilar(item); }

    // --------------------------------------------------------------------------------------------
    @Override
    public void whenUsed(PlayerInteractEvent e) {
        e.setCancelled(true);
        final LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());
        if(!cooldown.test(localPlayer)){
            localPlayer.msg("&e(Dragon) &cPlease wait, {0} second(s) left", cooldown.remainingTime(localPlayer, TimeUnit.SECONDS));
            return;
        }

        localPlayer.metadata().put(MetadataProvider.PLAYER_NO_FALL_DAMAGE, ExpiringValue.of(true, 10, TimeUnit.SECONDS));

        final Vector vec = localPlayer.toBukkit().getLocation().getDirection().multiply(strength);
        localPlayer.toBukkit().setVelocity(vec);

        localPlayer.msg("&e(Dragon) You have been pushed (strength: {0})", strength);
    }

    @Override
    public void handleListeners() {
        Events.subscribe(EntityDamageEvent.class)
                .filter(e -> e.getCause() == EntityDamageEvent.DamageCause.FALL)
                .filter(AbilityPredicates.humanHasAbility(this))
                .filter(AbilityPredicates.humanHasMetadata(MetadataProvider.PLAYER_NO_FALL_DAMAGE))
                .handler(e -> {
                    final LocalPlayer localPlayer = LocalPlayer.get((Player) e.getEntity());

                    localPlayer.metadata().remove(MetadataProvider.PLAYER_NO_FALL_DAMAGE);
                    localPlayer.msg("&e(Dragon) &eRemoving metadata...");
                    e.setCancelled(true);
                });
    }
}
