package me.aiglez.lonkskit.abilities.external.functionnal;

import com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent;
import me.aiglez.lonkskit.abilities.FunctionalAbility;
import me.aiglez.lonkskit.events.KitSelectEvent;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.lucko.helper.Events;
import me.lucko.helper.config.ConfigurationNode;
import org.apache.commons.lang.math.RandomUtils;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Disguises extends FunctionalAbility {
    private List<Player> mooshrooms = new ArrayList<Player>();
    private List<Player> creepers = new ArrayList<Player>();
    private List<Player> spiders = new ArrayList<Player>();
    private List<Player> snowmen = new ArrayList<Player>();
    private int random = RandomUtils.nextInt(5);
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
                .filter(e -> mooshrooms.contains(e.getPlayer()))
                .filter(e -> e.getTo().getBlock().getType() == Material.MYCELIUM || e.getFrom().getBlock().getType() == Material.MYCELIUM )
                .handler(e -> {
                    random = RandomUtils.nextInt(5);
                    if (random == 4){
                        e.getPlayer().setHealth(e.getPlayer().getHealth() + 2);
                        e.getPlayer().sendMessage("YOU RECEIVE 1 <3 ");
                    }
                    if (snowmen.contains(e.getPlayer()) ){
                        if (e.getTo().getBlock().getType() == Material.ICE || e.getTo().getBlock().getType() == Material.BLUE_ICE || e.getTo().getBlock().getType() == Material.FROSTED_ICE){
                            if (random == 0){
                                ItemStack itemStack = new ItemStack(Material.SNOWBALL);
                                itemStack.addEnchantment(Enchantment.ARROW_KNOCKBACK,4);
                                e.getPlayer().getInventory().addItem(itemStack);
                            }
                        }
                    }
                });
        //CREEPER Ability
        Events.subscribe(EntityDamageByEntityEvent.class)
                .filter(e -> e.getEntity() instanceof Player && e.getDamager() instanceof Player)
                .filter(e -> creepers.contains((Player)e.getDamager()))
                .handler(e ->{
                    random = RandomUtils.nextInt(5);
                   if (random == 3){
                       ((Player)e.getEntity()).setVelocity(e.getEntity().getLocation().getDirection().multiply(3).setY(2));
                   }
                });
        //SNOWMAN Ability
        Events.subscribe(ProjectileHitEvent.class)
                .filter(e -> snowmen.contains((Player)e.getEntity().getShooter()))
                .filter(e -> e.getEntity() instanceof Snowball)
                .handler(e ->{
                    Player victim = (Player) e.getHitEntity();
                    victim.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,60,5));
                    victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,60,4000));

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





















}
/* if(mooshrooms.contains(e.getLocalPlayer().toBukkit())) mooshrooms.remove(e.getLocalPlayer().toBukkit());
                    mooshrooms.add(e.getLocalPlayer().toBukkit());
                    DisguiseAPI.disguiseToAll(e.getLocalPlayer().toBukkit(), new MobDisguise(DisguiseType.MUSHROOM_COW)); */
