package me.aiglez.lonkskit.abilities.itembased;

import com.google.common.collect.Sets;
import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.MetadataProvider;
import me.aiglez.lonkskit.utils.items.ItemStackBuilder;
import me.lucko.helper.Events;
import me.lucko.helper.config.ConfigurationNode;
import me.lucko.helper.metadata.ExpiringValue;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LightningStrike;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author AigleZ
 * @date 30/09/2020 at 21:05
 */
public class ThorAbility extends ItemStackAbility {

    private final Set<Material> transparentMaterials = Sets.newHashSet(Material.AIR, Material.CAVE_AIR, Material.VOID_AIR);

    public ThorAbility(ConfigurationNode configuration) {
        super("thor", configuration);
        this.item = ItemStackBuilder.of(Material.IRON_AXE)
                .name(configuration.getNode("item-name").getString("Thor"))
                .enchant(Enchantment.DAMAGE_ALL, 1)
                .enchant(Enchantment.DURABILITY, 3)
                .build();
    }

    // --------------------------------------------------------------------------------------------
    public void whenRightClicked(PlayerInteractEvent e) {
        e.setCancelled(true);
        final LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());

        if(!cooldown.test(localPlayer)){
            localPlayer.msg("&b[LonksKit] &cPlease wait, {0} second(s) left", cooldown.remainingTime(localPlayer, TimeUnit.SECONDS));
            return;
        }

        applyEffects(localPlayer);

        final Block target = localPlayer.toBukkit().getTargetBlock(transparentMaterials, configuration.getNode("max-radius").getInt(100));

        localPlayer.metadata().put(MetadataProvider.PLAYER_NO_LIGHTING_DAMAGE, ExpiringValue.of(true, 3, TimeUnit.SECONDS));
        localPlayer.toBukkit().getWorld().strikeLightning(target.getLocation());
    }

    @Override
    public void whenLeftClicked(PlayerInteractEvent e) { }

    @Override
    public void registerListeners() {
        Events.subscribe(EntityDamageByEntityEvent.class)
                .filter(e -> e.getCause() == EntityDamageEvent.DamageCause.LIGHTNING || e.getDamager() instanceof LightningStrike)
                .filter(AbilityPredicates.possiblyHasMetadata(MetadataProvider.PLAYER_NO_LIGHTING_DAMAGE))
                .handler(e -> {
                    e.setDamage(0);
                    e.setCancelled(true);
                });

    }
}
