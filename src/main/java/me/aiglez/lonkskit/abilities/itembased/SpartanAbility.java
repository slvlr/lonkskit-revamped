package me.aiglez.lonkskit.abilities.itembased;

import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.MetadataProvider;
import me.lucko.helper.Events;
import me.lucko.helper.config.yaml.YAMLConfigurationLoader;
import me.lucko.helper.metadata.Metadata;
import me.lucko.helper.metadata.SoftValue;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

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
                .filter(AbilityPredicates.entityHasMetadata(MetadataProvider.SPARTAN_STICK))
                .handler(e -> {
                    ((Player)e.getHitEntity()).setWalkSpeed(0.9F);
                    ((Player) e.getHitEntity()).setJumping(true);
                    Objects.requireNonNull(e.getHitEntity()).setVelocity(e.getHitEntity().getVelocity().setY(2).add(new Vector(0,10,0)));
                    e.getHitEntity().setVelocity(new Vector(0,40,0));
                    Metadata.provideForEntity(e.getEntity()).clear();
                    e.getHitEntity().sendMessage("done");
                    e.getEntity().remove();
                    e.getHitEntity().sendMessage("by E");
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
        trident.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
        Metadata.provideForEntity(trident).put(MetadataProvider.SPARTAN_STICK, SoftValue.of(true));
        localPlayer.msg("metadata updated");
        trident.setDamage(getConfiguration().getNode("damage").getDouble(10));
        trident.setShooter(e.getPlayer());
    }

    @Override
    public void whenLeftClicked(PlayerInteractEvent e) {

    }
}
