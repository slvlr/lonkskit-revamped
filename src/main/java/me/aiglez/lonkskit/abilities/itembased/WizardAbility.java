package me.aiglez.lonkskit.abilities.itembased;

import me.aiglez.lonkskit.WorldProvider;
import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.Logger;
import me.aiglez.lonkskit.utils.MetadataProvider;
import me.aiglez.lonkskit.utils.items.ItemStackBuilder;
import me.lucko.helper.Events;
import me.lucko.helper.config.ConfigurationNode;
import me.lucko.helper.event.filter.EventFilters;
import me.lucko.helper.metadata.Metadata;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Snowball;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.TimeUnit;

/**
 * @author AigleZ
 * @date 04/10/2020
 */
public class WizardAbility extends ItemStackAbility {

    private final ItemStack item;

    public WizardAbility(ConfigurationNode configuration) {
        super("wizard", configuration);
        this.item = ItemStackBuilder.of(Material.BLAZE_ROD)
                .name("&6Wand")
                .build();
    }

    @Override
    public ItemStack getItemStack() { return this.item; }

    @Override
    public boolean isItemStack(ItemStack item) { return this.item.isSimilar(item); }

    // too slow
    // --------------------------------------------------------------------------------------------
    @Override
    public void whenUsed(PlayerInteractEvent e) {
        e.setCancelled(true);
        final LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());

        if(!cooldown.test(localPlayer)){
            localPlayer.msg("&cPlease wait, {0} second(s) left", cooldown.remainingTime(localPlayer, TimeUnit.SECONDS));
            return;
        }

        final Snowball snowball = localPlayer.toBukkit().launchProjectile(Snowball.class);
        snowball.setShooter(localPlayer.toBukkit());
        Metadata.provideForEntity(snowball).put(MetadataProvider.SNOWBALL_EXPLODE, true);
    }

    @Override
    public void handleListeners() {
        Events.subscribe(ProjectileHitEvent.class)
                .filter(e -> e.getEntityType() == EntityType.SNOWBALL)
                .filter(EventFilters.entityHasMetadata(MetadataProvider.SNOWBALL_EXPLODE))
                .handler(e -> {
                    Location explosionLocation = null;
                    if(e.getHitEntity() == null) {
                        explosionLocation = e.getHitBlock().getLocation();
                    } else if(e.getHitBlock() == null) {
                        explosionLocation = e.getHitEntity().getLocation();
                    } else {
                        Logger.severe("[Wizard] The snowball didn't shoot any block/player");
                    }

                    if(explosionLocation != null) {
                        WorldProvider.KP_WORLD.createExplosion(explosionLocation, 2F, false, false);
                    }
                    Metadata.provideForEntity(e.getEntity()).remove(MetadataProvider.SNOWBALL_EXPLODE);
                });
        }
}
