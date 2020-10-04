package me.aiglez.lonkskit.abilities.external.itembased;

import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.utils.items.ItemStackBuilder;
import me.lucko.helper.config.ConfigurationNode;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BeastmasterAbility extends ItemStackAbility {
    ItemStack item;
    private List<Player> ownersOfWolves = new ArrayList<Player>();
    protected BeastmasterAbility(ConfigurationNode configuration) {
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
    public void whenUsed(PlayerInteractEvent e) {
        if (e.getPlayer().getInventory().getItemInMainHand().isSimilar(this.item)) {
            if (e.getAction() == Action.RIGHT_CLICK_AIR){
                Player player = e.getPlayer();
                if (!ownersOfWolves.contains(player)){
                    //ANSAWBHA MN B3D

                }

            }
        }
    }

    @Override
    public void handleListeners() {

    }
}
