package me.aiglez.lonkskit.abilities.itembased;

import me.aiglez.lonkskit.WorldProvider;
import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.MetadataProvider;
import me.lucko.helper.Events;
import me.lucko.helper.config.yaml.YAMLConfigurationLoader;
import me.lucko.helper.metadata.Metadata;
import me.lucko.helper.metadata.SoftValue;
import org.bukkit.entity.Snowball;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author Fucking Johan
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
        Metadata.provideForEntity(snowball).put(MetadataProvider.SNOWBALL_EXPLODE, localPlayer);
        e.getPlayer().sendMessage("metadata snowball added");
    }

    @Override
    public void whenLeftClicked(PlayerInteractEvent e) { }

    @Override
    public void registerListeners() {
        Events.subscribe(ProjectileHitEvent.class)
                .filter(AbilityPredicates.entityHasMetadata(MetadataProvider.SNOWBALL_EXPLODE))
                .handler(e -> {
                    try {
                        WorldProvider.KP_WORLD.createExplosion(e.getEntity().getLocation(),  getConfiguration().getNode("explosion-power").getInt(3), false, false);
                        final LocalPlayer localPlayer = Metadata.provideForEntity(e.getEntity()).get(MetadataProvider.SNOWBALL_EXPLODE).get();
                        e.getEntity().getLocation().getNearbyPlayers(3,3,3).stream()
                                .filter(player -> player.getUniqueId().equals(localPlayer.getUniqueId()))
                                .forEach(player -> {
                                    player.sendMessage("&b[DEBUG] &cYou have entered in combat with " + localPlayer.getLastKnownName());
                                    localPlayer.msg("&b[DEBUG] &cYou have entered in combat with {0}.",player.getDisplayName());
                                });
                    }catch (NullPointerException exception){
                        WorldProvider.KP_WORLD.createExplosion(e.getEntity().getLocation(), getConfiguration().getNode("explosion-power").getInt(3
                         ), false, false);
                        Metadata.provideForEntity(e.getEntity()).remove(MetadataProvider.SNOWBALL_EXPLODE);
                    }
                });
        }
}
