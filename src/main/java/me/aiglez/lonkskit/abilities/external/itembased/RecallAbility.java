package me.aiglez.lonkskit.abilities.external.itembased;

import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.abilities.external.itembased.helpers.RecallHelper;
import me.aiglez.lonkskit.utils.items.ItemStackBuilder;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.config.ConfigurationNode;
import net.minecraft.server.v1_16_R2.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class RecallAbility extends ItemStackAbility {
    private Map<Player, EntityPlayer> npcMap = new HashMap<Player, EntityPlayer>();
    ItemStack item;
    public RecallAbility(ConfigurationNode configuration) {
        super("recall", configuration);
        this.item = ItemStackBuilder.of(Material.PLAYER_HEAD)
                .build();
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
        if (!npcMap.containsKey(e.getPlayer())){
            npcMap.put(e.getPlayer(),RecallHelper.createNpc(e.getPlayer()));
            long seconds = Long.parseLong(String.valueOf(getConfiguration().getNode("duration").getInt() * 20));
            Schedulers.sync().runLater(new Runnable() {
                @Override
                public void run() {
                    RecallHelper.removeNPCPacket(  npcMap.get(e.getPlayer()));
                    npcMap.remove(e.getPlayer(),npcMap.get(e.getPlayer()));
                }
            },seconds);
        }else {
            e.getPlayer().teleport(new Location(
                    Bukkit.getWorld(npcMap.get(e.getPlayer()).getWorld().getWorld().getName()),
                    npcMap.get(e.getPlayer()).locX(),
                    npcMap.get(e.getPlayer()).locY(),
                    npcMap.get(e.getPlayer()).locZ()));
        }
    }

    @Override
    public void whenLeftClicked(PlayerInteractEvent e) {

    }

    @Override
    public void handleListeners() {
        Events.subscribe(PlayerJoinEvent.class)
                .filter(e -> RecallHelper.getNPC() != null)
                .filter(e -> !RecallHelper.getNPC().isEmpty())
                .handler(e -> RecallHelper.addJoinPacket(e.getPlayer()));

    }
}
