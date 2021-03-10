package me.aiglez.lonkskit.abilities.functional;

import com.google.common.base.Preconditions;
import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.FunctionalAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.MetadataProvider;
import me.lucko.helper.Events;
import me.lucko.helper.config.yaml.YAMLConfigurationLoader;
import me.lucko.helper.metadata.SoftValue;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.*;
import me.lucko.helper.metadata.Metadata;
import java.io.IOException;

public class KillerAbility extends FunctionalAbility {

    public KillerAbility(YAMLConfigurationLoader configurationLoader) throws IOException {
        super("killer", configurationLoader);
    }

    @Override
    public void registerListeners() {
        Events.subscribe(PlayerDeathEvent.class)
                .filter(e -> e.getEntity() instanceof Player)
                .filter(AbilityPredicates.hasAbilityV(this))
                .handler(e -> {
                    Preconditions.checkNotNull(e.getEntity().getLocation().getWorld());
                    TNTPrimed tnt = (TNTPrimed) e.getEntity().getLocation().getWorld()
                            .spawnEntity(e.getEntity().getLocation(), EntityType.PRIMED_TNT, CreatureSpawnEvent.SpawnReason.CUSTOM,tn -> Metadata.provideForEntity(tn).put(MetadataProvider.KILLER_TNT, LocalPlayer.get(e.getEntity())));
                    tnt.setYield(getConfiguration().getNode("yield").getFloat(4.0F));
                    tnt.setFuseTicks(5);
                    applyEffects(LocalPlayer.get(e.getEntity()));
                });
        Events.subscribe(EntityExplodeEvent.class, EventPriority.HIGHEST)
                .handler(e -> e.blockList().clear());
        Events.subscribe(EntityDamageByEntityEvent.class)
                .filter(e -> e.getDamager() instanceof TNTPrimed)
                .filter(e -> e.getEntity() instanceof Player)
                .filter(e -> Metadata.getForEntity(e.getDamager()).get().has(MetadataProvider.KILLER_TNT))
                .handler(this::accept);
    }

    private void accept(EntityDamageByEntityEvent e) {
        e.setCancelled(true);
        e.getEntity().sendMessage("WELL");
        ((Player) e.getEntity()).setHealth(((Player) e.getEntity()).getHealth() - getConfiguration().getNode("damage").getInt());
        e.getEntity().sendMessage(ChatColor.translateAlternateColorCodes('&', "&b[DEBUG] &cYou have entered in combat with " + Metadata.provideForEntity(e.getDamager()).get(MetadataProvider.KILLER_TNT).get().getLastKnownName()));
        Metadata.provideForEntity(e.getDamager()).remove(MetadataProvider.KILLER_TNT);
    }
}
