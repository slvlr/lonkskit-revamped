package me.aiglez.lonkskit.abilities.itembased;

import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.MetadataProvider;
import me.lucko.helper.Events;
import me.lucko.helper.config.yaml.YAMLConfigurationLoader;
import me.lucko.helper.metadata.Metadata;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Explosive;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ShadowbladeAbility extends ItemStackAbility {

    public ShadowbladeAbility(YAMLConfigurationLoader configurationLoader) throws IOException {
        super("shadowblade", configurationLoader);
    }

    @Override
    public void whenRightClicked(PlayerInteractEvent e) {

    }

    @Override
    public void whenLeftClicked(PlayerInteractEvent e) {

    }

    @Override
    public void registerListeners() {
        Events.subscribe(PlayerInteractEvent.class)
                .filter(AbilityPredicates.hasAbility(this))
                .filter(e -> e.getMaterial() == Material.IRON_SWORD)
                .filter(e -> e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)
                .handler(e -> {
                    e.setCancelled(true);
                    final LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());
                    if (!cooldown.test(localPlayer)) {
                        localPlayer.msg("&b[LonksKit] &cPlease wait, {0} second(s) left", cooldown.remainingTime(localPlayer, TimeUnit.SECONDS));
                        return;
                    }
                    Fireball fireball = e.getPlayer().launchProjectile(Fireball.class);
                    fireball.setGravity(false);
                    fireball.setShooter(e.getPlayer());
                    Metadata.provideForEntity(fireball).put(MetadataProvider.SHADOW_FIREBALL,Boolean.TRUE);
                    applyEffects(localPlayer);
                });
        Events.subscribe(EntityExplodeEvent.class)
                .filter(q-> q.getEntity() instanceof Fireball || q.getEntity() instanceof Explosive)
                .handler(e -> e.blockList().clear());
        Events.subscribe(EntityDamageByEntityEvent.class, EventPriority.HIGHEST)
                .filter(e -> e.getDamager() instanceof Fireball)
                .filter(e -> e.getEntity() instanceof Player)
                .filter(e -> Metadata.provideForEntity(e.getDamager()).has(MetadataProvider.SHADOW_FIREBALL))
                .handler(e-> {
                    e.setDamage(getConfiguration().getNode("damage").getDouble(1D));
                    Player shooter = ((Player) ((Fireball)e.getDamager()).getShooter());
                    e.getEntity().sendMessage(ChatColor.translateAlternateColorCodes('&',"&b[DEBUG] &cYou have entered in combat with " + shooter.getDisplayName()));
                    shooter.sendMessage(ChatColor.translateAlternateColorCodes('&',"&b[DEBUG] &cYou have entered in combat with " + ((Player)e.getEntity()).getDisplayName()));
                });
    }
}
