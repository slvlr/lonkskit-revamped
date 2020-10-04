package me.aiglez.lonkskit.abilities.itembased;

import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.Logger;
import me.aiglez.lonkskit.utils.MetadataProvider;
import me.aiglez.lonkskit.utils.items.ItemStackBuilder;
import me.lucko.helper.Events;
import me.lucko.helper.config.ConfigurationNode;
import me.lucko.helper.event.filter.EventFilters;
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
            localPlayer.msg("&cPlease wait, {0} second(s) left", cooldown.remainingTime(localPlayer, TimeUnit.SECONDS));
            return;
        }

        localPlayer.metadata().put(MetadataProvider.PLAYER_NO_FALL_DAMAGE, ExpiringValue.of(true, 10, TimeUnit.SECONDS));

        final Vector vec = localPlayer.toBukkit().getLocation().getDirection().multiply(strength);
        localPlayer.toBukkit().setVelocity(vec);

        localPlayer.msg("&b[Debug] &fYou have been pushed (strength: {0})", strength);
    }

    @Override
    public void registerListeners() {
        EventFilters
        Events.subscribe(EntityDamageEvent.class)
                .filter(EventFilters.ignoreCancelled())
                .filter(e -> e.getCause() == EntityDamageEvent.DamageCause.FALL)
                .filter(AbilityPredicates.humanHasAbility(this))

                .handler(e -> {
                    final LocalPlayer localPlayer = LocalPlayer.get((Player) e.getEntity());

                    localPlayer.toBukkit().getWorld().getNearbyEntities(
                            localPlayer.toBukkit().getLocation(),
                            5, 5, 5,
                            entity -> entity instanceof Player
                    ).forEach(entity -> {
                        final LocalPlayer under = LocalPlayer.get(((Player) entity));
                        if(!(under.getUniqueId().equals(localPlayer.getUniqueId()))) {
                            double damage;
                            if(under.toBukkit().isSneaking()) { // is player sneaking
                                damage = Math.min(e.getFinalDamage(), 4.5);
                                Logger.debug("[Stomp/Portastomp] Damaging a player with " + damage + " damage (SNEAKING)");
                            } else {
                                damage = e.getFinalDamage();
                                Logger.debug("[Stomp/Portastomp] Damaging a player with " + damage + " damage");
                            }

                            under.toBukkit().damage(damage);
                            localPlayer.msg("&eDamaging the player {0} with {1} damage", under.getLastKnownName(), damage);
                        }
                    });
                    Logger.debug("[Stomp/Portastomp] Setting damage of player who use the ability to " + Math.min(e.getDamage(), 2.5D) );
                    e.setDamage(Math.min(e.getDamage(), 2.5D));
                });
    }
}
