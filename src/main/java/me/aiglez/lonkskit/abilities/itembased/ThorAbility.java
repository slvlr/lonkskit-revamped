package me.aiglez.lonkskit.abilities.itembased;

import com.google.common.collect.Sets;
import me.aiglez.lonkskit.WorldProvider;
import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.MetadataProvider;
import me.lucko.helper.Events;
import me.lucko.helper.config.ConfigurationNode;
import me.lucko.helper.event.filter.EventFilters;
import me.lucko.helper.metadata.ExpiringValue;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import me.aiglez.lonkskit.utils.items.ItemStackBuilder;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author AigleZ
 * @date 30/09/2020 at 21:05
 */
public class ThorAbility extends ItemStackAbility {

    private final ItemStack item;
    private final Set<Material> transparentMaterials = Sets.newHashSet(Material.AIR, Material.CAVE_AIR, Material.VOID_AIR);

    public ThorAbility(ConfigurationNode configuration) {
        super("thor", configuration);
        this.item = ItemStackBuilder.of(Material.IRON_AXE)
                .name("&bThor's Hammer")
                .enchant(Enchantment.DAMAGE_ALL, 1)
                .enchant(Enchantment.DURABILITY, 3)
                .build();
    }

    @Override
    public ItemStack getItemStack() { return item; }

    @Override
    public boolean isItemStack(ItemStack item) { return this.item.isSimilar(item); }

    // --------------------------------------------------------------------------------------------
    public void whenUsed(PlayerInteractEvent e) {
        e.setCancelled(true);
        final LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());

        if(!cooldown.test(localPlayer)){
            localPlayer.msg("&3(Thor) &cPlease wait, {0} second(s) left", cooldown.remainingTime(localPlayer, TimeUnit.SECONDS));
            return;
        }

        final Block target = localPlayer.toBukkit().getTargetBlock(transparentMaterials, configuration.getNode("max-radius").getInt(100));

        localPlayer.metadata().put(MetadataProvider.PLAYER_NO_LIGHTING_DAMAGE, ExpiringValue.of(true, 2, TimeUnit.SECONDS));
        localPlayer.toBukkit().getWorld().strikeLightning(WorldProvider.KP_WORLD.getHighestBlockAt(target.getLocation()).getLocation());
    }

    @Override
    public void handleListeners() {
        Events.subscribe(EntityDamageEvent.class)
                .filter(EventFilters.ignoreCancelled())
                .filter(e -> e.getCause() == EntityDamageEvent.DamageCause.LIGHTNING)
                .filter(AbilityPredicates.humanHasAbility(this))
                .filter(AbilityPredicates.humanHasMetadata(MetadataProvider.PLAYER_NO_LIGHTING_DAMAGE))
                .handler(e -> {
                    e.setDamage(0);
                    e.setCancelled(true);
                });
    }
}
