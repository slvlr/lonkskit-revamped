package me.aiglez.lonkskit.abilities.itembased;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.aiglez.lonkskit.WorldProvider;
import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.MetadataProvider;
import me.lucko.helper.Events;
import me.lucko.helper.config.ConfigurationNode;
import me.lucko.helper.metadata.Metadata;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftCreature;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Map;
import java.util.Set;

/**
 * @author AigleZ
 * @date 09/10/2020
 */
public class NecromancerAbility extends ItemStackAbility {

    private final Map<LocalPlayer, Set<Monster>> helpers;

    public NecromancerAbility(ConfigurationNode configuration) {
        super("necromancer", configuration);
        this.helpers = Maps.newHashMap();
    }

    @Override
    public void whenRightClicked(PlayerInteractEvent e) {
        e.setCancelled(true);
        final LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());

        final Zombie zombie = (Zombie) WorldProvider.KP_WORLD.spawnEntity(localPlayer.getLocation(), EntityType.SKELETON);

        zombie.setCustomName(ChatColor.AQUA + localPlayer.getLastKnownName() + "'s zombie");
        zombie.setConversionTime(-1);
        zombie.setArmsRaised(true);
        zombie.setShouldBurnInDay(false);

        ((CraftCreature) zombie).getHandle().getNavigation().d(false);

        Metadata.provideForEntity(zombie).put(MetadataProvider.NECROMANCER_ENTITY, localPlayer);
        addHelper(localPlayer, zombie);
    }

    @Override
    public void whenLeftClicked(PlayerInteractEvent e) {
        e.setCancelled(true);
    }

    @Override
    public void registerListeners() {
        Events.subscribe(EntityDamageByEntityEvent.class)
                .handler(e -> {
                    final Entity entityDamager = e.getDamager();
                    final Entity entityVictim = e.getEntity();

                    if(entityDamager instanceof Player) {
                        final LocalPlayer damager = LocalPlayer.get((Player) e.getDamager());
                        final LivingEntity victim = (LivingEntity) e.getEntity();
                        if(Metadata.provideForEntity(victim).has(MetadataProvider.NECROMANCER_ENTITY)) {
                            damager.msg("&7(Necromancer - Debug) &cYou can't damage a helper");

                            e.setCancelled(true);
                            return;
                        }

                        getHelpers(damager)
                                .forEach(monster -> {
                                    monster.setTarget((LivingEntity) e.getEntity());
                                });

                        damager.msg("&7(Necromancer - Debug) &fYou attacked &b{0} &fyour helpers will attack him now");
                    } else {

                    }
                });



    }

    private Set<Monster> getHelpers(LocalPlayer localPlayer) {
        return helpers.get(localPlayer);
    }

    private void addHelper(LocalPlayer localPlayer, Monster monster) {
        if(helpers.get(localPlayer) == null) {
            helpers.put(localPlayer, Sets.newHashSet(monster));
        } else {
            helpers.get(localPlayer).add(monster);
        }
    }

    private void removeHelper(LocalPlayer localPlayer, Monster monster) {
        if(helpers.get(localPlayer) != null) {
            helpers.get(localPlayer).remove(monster);
        }
    }
}
