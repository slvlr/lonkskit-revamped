package me.aiglez.lonkskit.abilities.functional.johan.disguises;

import me.aiglez.lonkskit.WorldProvider;
import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.FunctionalAbility;
import me.aiglez.lonkskit.abilities.functional.johan.DisguiseAbilities;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.MetadataProvider;
import me.aiglez.lonkskit.utils.Various;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.config.yaml.YAMLConfigurationLoader;
import me.lucko.helper.metadata.Metadata;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.IOException;

import static me.aiglez.lonkskit.abilities.functional.johan.DisguiseAbilities.snowmen;
import static me.aiglez.lonkskit.abilities.functional.johan.DisguiseAbilities.victimsOfSnow;

public class SnowmanAbility extends FunctionalAbility {
    private static int times;

    public SnowmanAbility(YAMLConfigurationLoader configurationLoader) throws IOException {
        super("snowman", configurationLoader);
    }

    @Override
    public void registerListeners() {
        Events.subscribe(PlayerInteractEvent.class)
                .filter(AbilityPredicates.hasAbility(this))
                .filter(PlayerInteractEvent::hasItem)
                .filter(e -> e.getItem() instanceof Snowball)
                .handler(e -> {
                    LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());
                    applyEffects(localPlayer);
                    final Snowball snowball = localPlayer.toBukkit().launchProjectile(Snowball.class);
                    snowball.setShooter(e.getPlayer());
                    Metadata.provideForEntity(snowball).put(MetadataProvider.SNOWMAN,Boolean.TRUE);
                });
        Events.subscribe(ProjectileHitEvent.class)
                .filter(e -> snowmen.contains((Player) e.getEntity().getShooter()))
                .filter(e -> e.getEntity() instanceof Snowball && e.getHitEntity() instanceof Player)
                .filter(AbilityPredicates.entityHasMetadata(MetadataProvider.SNOWMAN))
                .handler(e ->{
                    Player victim = (Player) e.getHitEntity();
                    assert victim != null;
                    int duration = 7 * 20;

                    victim.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,duration,666,false,false,false));
                    victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,duration,666,false,false,false));
                    Various.damage(LocalPlayer.get(victim),3,false);
                    victimsOfSnow.add(victim);
                    applyEffects(LocalPlayer.get((Player) e.getEntity().getShooter()));
                    Schedulers.sync()
                            .runLater(() -> victimsOfSnow.remove(victim), duration);
                });
        Events.subscribe(EntityDamageByEntityEvent.class)
                .filter(e -> e.getEntity() instanceof Player)
                .filter(e-> e.getEntity().getWorld() == WorldProvider.KP_WORLD)
                .filter(e -> victimsOfSnow.contains((Player) e.getEntity()))
                .handler(e-> e.setCancelled(true));
        Events.subscribe(PlayerMoveEvent.class)
                .handler(e -> {
                    if (victimsOfSnow.contains(e.getPlayer())) e.setCancelled(true);

                    if (snowmen.contains(e.getPlayer())) {
                        if (e.getTo().getBlock().getType() == Material.ICE ||
                                e.getTo().getBlock().getType() == Material.BLUE_ICE ||
                                e.getTo().getBlock().getType() == Material.FROSTED_ICE ||
                                e.getTo().getBlock().getRelative(0,-1,0).getType() == Material.ICE ||
                                e.getFrom().getBlock().getRelative(0,-1,0).getType() == Material.BLUE_ICE || e.getFrom().getBlock().getRelative(0,-1,0).getType() == Material.FROSTED_ICE ) {
                            if (times == 0) {
                                ItemStack itemStack = new ItemStack(Material.SNOWBALL);
                                itemStack.addEnchantment(Enchantment.ARROW_DAMAGE,4);
                                e.getPlayer().getInventory().addItem(itemStack);
                                e.getPlayer().sendMessage("You received A Good SnowBall");
                            }
                            if (times >= 24){
                                times = 0;
                            }
                            times++;

                        }
                    }

                });
    }
}
