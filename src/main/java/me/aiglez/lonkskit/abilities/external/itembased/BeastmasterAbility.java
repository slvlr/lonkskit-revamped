package me.aiglez.lonkskit.abilities.external.itembased;


import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.utils.items.ItemStackBuilder;
import me.lucko.helper.Events;
import me.lucko.helper.config.ConfigurationNode;
import net.minecraft.server.v1_16_R2.EntityCreature;
import net.minecraft.server.v1_16_R2.EntityTypes;
import net.minecraft.server.v1_16_R2.WorldServer;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftEntity;

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
    Map<Player,Wolf[]> getwolf = new HashMap<Player, Wolf[]>();
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


                    Wolf wolf = (Wolf) e.getPlayer().getLocation().getWorld().spawnEntity(e.getPlayer().getLocation(), EntityType.WOLF);
                    EntityTypes<? extends EntityCreature> type =
                            (EntityTypes<? extends EntityCreature>)((CraftEntity)wolf).getHandle().getEntityType();
                    BeastHelp pet = new BeastHelp(type,wolf.getLocation());
                    pet.setOwner(player);
                    pet.setName(player.getDisplayName() + "WOLF");
                    WorldServer world = ((CraftWorld)player.getWorld()).getHandle();
                    world.addEntity(pet);
                    wolf.remove();
                    ownersOfWolves.add(player);
                    getwolf.put(player,new Wolf[]{(Wolf) pet});
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
        Events.subscribe(EntityDamageByEntityEvent.class)
                .filter(e -> ownersOfWolves.contains(e.getEntity()))
                .handler(e->{
                   getwolf.get(e.getEntity())[0].attack(e.getDamager());

                });

    }
}
