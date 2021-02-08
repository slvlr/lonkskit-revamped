package me.aiglez.lonkskit.abilities.itembased;

import me.aiglez.lonkskit.WorldProvider;
import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.MetadataProvider;
import me.lucko.helper.Events;
import me.lucko.helper.config.yaml.YAMLConfigurationLoader;
import me.lucko.helper.metadata.Metadata;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Snowball;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author Fucking AigleZ
 * @date 04/10/2020
 */
public class WizardAbility extends ItemStackAbility {

    public WizardAbility(YAMLConfigurationLoader configurationLoader) throws IOException {
        super("wizard", configurationLoader);
    }

    // --------------------------------------------------------------------------------------------
    @Override
    public void whenRightClicked(PlayerInteractEvent e) {
        e.setCancelled(true);
        final LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());

        if(!cooldown.test(localPlayer)){
            localPlayer.msg("&b[LonksKit] &cPlease wait, {0} second(s) left", cooldown.remainingTime(localPlayer, TimeUnit.SECONDS));
            return;
        }

        applyEffects(localPlayer);

        final Snowball snowball = localPlayer.toBukkit().launchProjectile(Snowball.class);
        snowball.setShooter(localPlayer.toBukkit());
        Metadata.provideForEntity(snowball).put(MetadataProvider.SNOWBALL_EXPLODE, true);
    }

    @Override
    public void whenLeftClicked(PlayerInteractEvent e) { }

    @Override
    public void registerListeners() {
        Events.subscribe(ProjectileHitEvent.class)
                .filter(e -> e.getEntityType() == EntityType.SNOWBALL)
                .filter(AbilityPredicates.entityHasMetadata(MetadataProvider.SNOWBALL_EXPLODE))
                .handler(e -> {
                    Location explosionLocation = null;
                    if(e.getHitEntity() == null) {
                        explosionLocation = e.getHitBlock().getLocation();
                    } else if(e.getHitBlock() == null) {
                        explosionLocation = e.getHitEntity().getLocation();
                    }

                    if(explosionLocation != null) {
                        WorldProvider.KP_WORLD.createExplosion(explosionLocation,
                                Float.intBitsToFloat(getConfiguration().getNode("explosion-power").getInt(4))
                                , false, false, (Entity) e.getEntity().getShooter());;
                    }

                    Metadata.provideForEntity(e.getEntity()).remove(MetadataProvider.SNOWBALL_EXPLODE);
                });
        }
}
