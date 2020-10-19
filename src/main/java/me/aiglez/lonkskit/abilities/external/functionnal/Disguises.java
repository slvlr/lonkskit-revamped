package me.aiglez.lonkskit.abilities.external.functionnal;

import me.aiglez.lonkskit.KitPlugin;
import me.aiglez.lonkskit.abilities.FunctionalAbility;
import me.aiglez.lonkskit.events.KitSelectEvent;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.config.ConfigurationNode;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class Disguises extends FunctionalAbility {
    private final List<Player> mooshrooms = new ArrayList<Player>();
    private final List<Player> creepers = new ArrayList<Player>();
    private final List<Player> spiders = new ArrayList<Player>();
    private final List<Player> snowmen = new ArrayList<Player>();
    private final List<Player> victimsOfSnow = new ArrayList<Player>();
    private int times;
    private int times1;
    private int times2;

    public Disguises(ConfigurationNode configuration) {
        super("disguises", configuration);
    }

    @Override
    public void handleListeners() {
        Events.subscribe(KitSelectEvent.class)
                .filter(e -> e.getKit() != null)
                .handler(e ->{
                    DisguiseAPI.undisguiseToAll(e.getLocalPlayer().toBukkit());
                    String backendName = e.getKit().getBackendName().toUpperCase();
                    kitsSelect(e.getLocalPlayer().toBukkit(),backendName,"SPIDER","PIG","WOLF","CREEPER","MOOSHROOM","SNOWM");


                });
        //MOOSHROOM Ability
        Events.subscribe(PlayerMoveEvent.class)
                .handler(e -> {
                    if (victimsOfSnow.contains(e.getPlayer())) e.setCancelled(true);
                    if (mooshrooms.contains(e.getPlayer())) {
                        if (e.getTo().getBlock().getType() == Material.MYCELIUM || e.getFrom().getBlock().getType() == Material.MYCELIUM
                        || e.getTo().getBlock().getRelative(0,-1,0).getType() == Material.MYCELIUM ||  e.getFrom().getBlock().getRelative(0,-1,0).getType() == Material.MYCELIUM ) {
                            if (times1 == 0) {
                                if (e.getPlayer().getHealth() != 20) {
                                    e.getPlayer().setHealth(Math.min(20.00D,e.getPlayer().getHealth() + 1)); //KHLIHA HAKA W T2KD MN WRAITH TDIR DAKCHI B CONFIG FILES W LAY3AWN
                                }
                            }
                            if (times1 >= 20) {
                                times1 = 0;
                            }else
                                times1++;
                        }
                        if (snowmen.contains(e.getPlayer())) {
                            if (e.getTo().getBlock().getType() == Material.ICE ||
                                    e.getTo().getBlock().getType() == Material.BLUE_ICE ||
                                    e.getTo().getBlock().getType() == Material.FROSTED_ICE ||
                            e.getTo().getBlock().getRelative(0,-1,0).getType() == Material.ICE ||
                                    e.getFrom().getBlock().getRelative(0,-1,0).getType() == Material.BLUE_ICE || e.getFrom().getBlock().getRelative(0,-1,0).getType() == Material.FROSTED_ICE ) {
                                if (times == 0) {
                                    ItemStack itemStack = new ItemStack(Material.SNOWBALL);
                                    itemStack.addEnchantment(Enchantment.ARROW_DAMAGE,4);
                                    e.getPlayer().getInventory().setItem(27,itemStack);
                                    e.getPlayer().sendMessage("You received A Good SnowBall");
                                }
                                if (times >= 24){
                                    times = 0;
                                }else times++;

                            }
                        }
                    }
                    if (spiders.contains(e.getPlayer())){
                        if (nearWeb(e.getTo().clone()) || nearWeb(e.getTo().clone().add(0,1,0))){
                            if (!(e.getPlayer().hasPotionEffect(PotionEffectType.INCREASE_DAMAGE) && e.getPlayer().hasPotionEffect(PotionEffectType.SPEED))) {
                                e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 10 * 20, 1)); //Set It Customizable !!
                                e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10 * 20, 1));
                            }
                        }



                    }

                });

        //CREEPER Ability
        Events.subscribe(EntityDamageByEntityEvent.class)
                .filter(e -> e.getEntity() instanceof Player && e.getDamager() instanceof Player)
                .filter(e -> creepers.contains(e.getDamager()))
                .handler(e ->{
                    if (times2 == 0) {
                        e.setCancelled(true);
                        Bukkit.getScheduler().runTaskLater(KitPlugin.getSingleton(), () -> {
                            e.getEntity().setVelocity(e.getEntity().getLocation().getDirection().multiply(3).setY(2));
                        },1L);
                    }
                    if (times2 >= 10) {
                        times2 = 0;
                    }else
                        times2++;
                });
        //SNOWMAN Ability
        Events.subscribe(ProjectileHitEvent.class)
                .filter(e -> snowmen.contains(e.getEntity().getShooter()))
                .filter(e -> e.getEntity() instanceof Snowball && e.getHitEntity() instanceof Player)
                .handler(e ->{
                    Player victim = (Player) e.getHitEntity();
                    assert victim != null;
                    int duration = 7 * 20;
                    victim.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,duration,5));
                    victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,duration,5));
                    victim.damage(3);
                    victimsOfSnow.add(victim);
                    Schedulers.sync()
                                .runLater(() -> victimsOfSnow.remove(victim),Long.valueOf(duration));
                });
    }

    private void kitsSelect(Player player,String backendName,String spider,String pig,String wolf,String creeper,String mooshroom,String snowman){
        spiders.remove(player);
        mooshrooms.remove(player);
        creepers.remove(player);

        if (backendName.contains(pig)){
            DisguiseAPI.disguiseToAll(player,new MobDisguise(DisguiseType.PIG));
        }

        if (backendName.contains(wolf)){
            DisguiseAPI.disguiseToAll(player,new MobDisguise(DisguiseType.WOLF));
        }

        if (backendName.contains(creeper)){
            DisguiseAPI.disguiseToAll(player,new MobDisguise(DisguiseType.CREEPER));
            creepers.add(player);
        }

        if (backendName.contains(spider)){
            DisguiseAPI.disguiseToAll(player,new MobDisguise(DisguiseType.SPIDER));
            spiders.add(player);
        }

        if (backendName.contains(mooshroom)){
            DisguiseAPI.disguiseToAll(player,new MobDisguise(DisguiseType.MUSHROOM_COW));
            mooshrooms.add(player);
        }

        if (backendName.contains(snowman)){
            DisguiseAPI.disguiseToAll(player,new MobDisguise(DisguiseType.SNOWMAN));
            snowmen.add(player);

        }

    }

    private boolean nearWeb(Location loc) {
        double range = .3;
        if (loc.add(0, 0, 0).getBlock().getType().equals(Material.COBWEB)
                || loc.add(range, 0, 0).getBlock().getType().equals(Material.COBWEB)
                || loc.add(-range, 0, 0).getBlock().getType().equals(Material.COBWEB)
                || loc.add(range, 0, range).getBlock().getType().equals(Material.COBWEB)
                || loc.add(-range, 0, range).getBlock().getType().equals(Material.COBWEB)
                || loc.add(-range, 0, -range).getBlock().getType().equals(Material.COBWEB)
                || loc.add(0, 0, range).getBlock().getType().equals(Material.COBWEB)
                || loc.add(0, 0, -range).getBlock().getType().equals(Material.COBWEB))
            return true;
        return false;
    }


















}
/* if(mooshrooms.contains(e.getLocalPlayer().toBukkit())) mooshrooms.remove(e.getLocalPlayer().toBukkit());
                    mooshrooms.add(e.getLocalPlayer().toBukkit());
                    DisguiseAPI.disguiseToAll(e.getLocalPlayer().toBukkit(), new MobDisguise(DisguiseType.MUSHROOM_COW)); */
