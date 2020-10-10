package me.aiglez.lonkskit.abilities.external.itembased;


import me.aiglez.lonkskit.WorldProvider;
import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.abilities.external.itembased.helpers.BeastmasterWolfEntity;
import me.aiglez.lonkskit.utils.items.ItemStackBuilder;
import me.lucko.helper.Events;
import me.lucko.helper.config.ConfigurationNode;
import net.minecraft.server.v1_16_R2.EntityCreature;
import net.minecraft.server.v1_16_R2.EntityLiving;
import net.minecraft.server.v1_16_R2.EntityTypes;
import net.minecraft.server.v1_16_R2.WorldServer;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftEntity;

import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeastmasterAbility extends ItemStackAbility {
    ItemStack item;
    Map<Player, BeastHelp[]> getwolf = new HashMap<Player, BeastHelp[]>();
    private List<Player> ownersOfWolves = new ArrayList<Player>();
    public BeastmasterAbility(ConfigurationNode configuration) {
        super("beastmaster", configuration);
        this.item = ItemStackBuilder.of(Material.BONE).build();
    }

    @Override
    public ItemStack getItemStack() {
        return item;
    }

    @Override
    public boolean isItemStack(ItemStack item) {
        return this.item.isSimilar(item);
    }

    @Override
    public void whenRightClicked(PlayerInteractEvent e) {
        if (e.getPlayer().getInventory().getItemInMainHand().isSimilar(this.item)) {
            if (e.getAction() == Action.RIGHT_CLICK_AIR){
                Player player = e.getPlayer();
                if (!ownersOfWolves.contains(player)){
                    WorldServer world = ((CraftWorld) player.getWorld()).getHandle();
                    BeastmasterWolfEntity customWolf = new BeastmasterWolfEntity(player, WorldProvider.KP_WORLD);
                    customWolf.setPosition(player.getLocation().getX(),player.getLocation().getY(),player.getLocation().getZ());
                    world.addEntity(customWolf);

                    return;

                }

            }
        }
    }

    @Override
    public void whenLeftClicked(PlayerInteractEvent e) {

    }


    @Override
    public void handleListeners() {

    }
}
