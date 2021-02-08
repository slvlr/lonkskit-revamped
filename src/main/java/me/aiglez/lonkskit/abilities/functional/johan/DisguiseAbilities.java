package me.aiglez.lonkskit.abilities.functional.johan;

import com.google.common.collect.Lists;
import me.aiglez.lonkskit.KitPlugin;
import me.aiglez.lonkskit.abilities.FunctionalAbility;
import me.aiglez.lonkskit.events.KitSelectEvent;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.config.ConfigurationNode;
import me.lucko.helper.config.yaml.YAMLConfigurationLoader;
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
import org.bukkit.potion.PotionEffectType;

import java.io.IOException;
import java.util.List;

public class DisguiseAbilities extends FunctionalAbility {

    public static final List<Player> mooshrooms = Lists.newArrayList();
    public static final List<Player> creepers = Lists.newArrayList();
    public static final List<Player> spiders = Lists.newArrayList();
    public static final List<Player> snowmen = Lists.newArrayList();
    public static final List<Player> victimsOfSnow = Lists.newArrayList();
    private int times;
    private int times1;
    private int times2;

    public DisguiseAbilities(YAMLConfigurationLoader configurationLoader) throws IOException {
        super("disguises", configurationLoader);
    }

    @Override
    public void registerListeners() {
        Events.subscribe(KitSelectEvent.class)
                .handler(e ->{
                    DisguiseAPI.undisguiseToAll(e.getLocalPlayer().toBukkit());
                    String backendName = e.getKit().getBackendName().toUpperCase();
                    kitsSelect(e.getLocalPlayer().toBukkit(),backendName,"SPIDER","PIG","WOLF","CREEPER","MOOSHROOM","SNOWM");


                });

        //CREEPER Ability

        //SNOWMAN Ability

    }

    private void kitsSelect(Player player,String backendName,String spider,String pig,String wolf,String creeper,String mooshroom,String snowman){
        spiders.remove(player);
        mooshrooms.remove(player);
        creepers.remove(player);
        snowmen.remove(player);

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

    public static boolean nearWeb(Location loc) {
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
