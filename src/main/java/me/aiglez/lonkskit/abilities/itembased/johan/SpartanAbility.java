package me.aiglez.lonkskit.abilities.itembased.johan;

import com.destroystokyo.paper.event.entity.ProjectileCollideEvent;
import com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent;
import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.MetadataProvider;
import me.lucko.helper.Events;
import me.lucko.helper.config.yaml.YAMLConfigurationLoader;
import me.lucko.helper.metadata.Metadata;
import org.bukkit.Material;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class SpartanAbility extends ItemStackAbility {

    public SpartanAbility(YAMLConfigurationLoader configurationLoader) throws IOException {
        super("spartan", configurationLoader);
    }

    @Override
    public void registerListeners() {
        Events.subscribe(ProjectileHitEvent.class)
                .filter(e -> e.getHitEntity() != null)
                .filter(e -> Metadata.provideForEntity(e.getEntity().getUniqueId()).has(MetadataProvider.SPARTAN_STICK))
                .handler(e -> {
                    Metadata.provideForEntity(e.getEntity()).clear();
                    e.getEntity().remove();
                    Objects.requireNonNull(e.getHitEntity()).getLocation().getDirection().multiply(3).setY(3);
                    e.getHitEntity().sendMessage("hite");
                });
        Events.subscribe(EntityDamageByEntityEvent.class)
                .filter(e -> e.getDamager().getType() == EntityType.TRIDENT)
                .filter(e -> Metadata.provideForEntity(e.getEntity().getUniqueId()).has(MetadataProvider.SPARTAN_STICK))
                .handler(e -> {
                    Metadata.provideForEntity(e.getEntity()).clear();
                    e.getDamager().remove();
                    Objects.requireNonNull(e.getEntity().getLocation().getDirection().multiply(3).setY(3));
                    e.getEntity().sendMessage("byE");
                });
        Events.subscribe(PlayerLaunchProjectileEvent.class, EventPriority.HIGHEST)
                .filter(e -> e.getPlayer().getInventory().getItemInMainHand().getType() == Material.TRIDENT)
                .handler(e -> {
                    e.setCancelled(true);
                    e.getPlayer().sendMessage("You can't launch trident!");
                });
    }
    @Override
    public void whenRightClicked(PlayerInteractEvent e) {
        LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());
        if (!cooldown.test(localPlayer)) {
            localPlayer.msg("&b[LonksKit] &cPlease wait, {0} second(s) left", cooldown.remainingTime(localPlayer, TimeUnit.SECONDS));
            return;
        }
        applyEffects(localPlayer);
        Trident trident = e.getPlayer().launchProjectile(Trident.class);
        trident.setDamage(getConfiguration().getNode("damage").getDouble());
        trident.setShooter(e.getPlayer());
        trident.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
        Metadata.provideForEntity(trident.getUniqueId()).put(MetadataProvider.SPARTAN_STICK,true);
    }

    @Override
    public void whenLeftClicked(PlayerInteractEvent e) {

    }
}
