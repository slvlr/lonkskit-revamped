package me.aiglez.lonkskit.abilities.external.functionnal;

import me.aiglez.lonkskit.abilities.FunctionalAbility;
import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.abilities.external.functionnal.helpers.RecallHelper;
import me.aiglez.lonkskit.events.KitSelectEvent;
import me.aiglez.lonkskit.utils.items.ItemStackBuilder;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.config.ConfigurationNode;
import net.minecraft.server.v1_16_R2.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

public class RecallAbility extends FunctionalAbility {
    private final Map<UUID, EntityPlayer> npcMap = new HashMap<UUID, EntityPlayer>();
    private final List<Player> teleportedPlayers = new ArrayList<Player>();
    private final List<Player> heads = new ArrayList<Player>();
    private final List<ItemStack> items = new ArrayList<ItemStack>();
    public static List<EntityPlayer> removedNPCS = new ArrayList<EntityPlayer>();

    public RecallAbility(ConfigurationNode configuration) {
        super("recall", configuration);

    }

    public void whenRightClicked(PlayerInteractEvent e) {

    }
    ////////////
    public void whenLeftClicked(PlayerInteractEvent e) {

    }

    @Override
    public void handleListeners() {
        Events.subscribe(PlayerJoinEvent.class)
                .filter(e -> RecallHelper.getNPC() != null)
                .filter(e -> !RecallHelper.getNPC().isEmpty())
                .handler(e -> {
                    RecallHelper.addJoinPacket(e.getPlayer());
                    for (EntityPlayer npc : removedNPCS){
                        RecallHelper.removeNPCPacket(npc);

                    }
                });
        Events.subscribe(KitSelectEvent.class)
                .filter(e -> e.getKit() != null)
                .filter(e ->{
                    String backendName = e.getKit().getBackendName().toUpperCase();
                    if (backendName.contains("RECALL")){
                        return true;
                    }else{
                        if (heads.contains(e.getLocalPlayer().toBukkit())) heads.remove(e.getLocalPlayer().toBukkit());
                    };
                    return false;
                })
                .handler(e ->{
                        Schedulers.sync()
                                .runLater(() -> {
                                    ItemStack head = new ItemStack(Material.PLAYER_HEAD);
                                    SkullMeta meta = (SkullMeta) head.getItemMeta();
                                    meta.setOwningPlayer(e.getLocalPlayer().toBukkit());
                                    head.setItemMeta(meta);
                                    heads.add(e.getLocalPlayer().toBukkit());
                                    items.add(head);
                                    e.getLocalPlayer().toBukkit().getInventory().setItem(1,head);
                                },5L);
                });
        Events.subscribe(PlayerInteractEvent.class)
                .filter(e -> heads.contains(e.getPlayer()))
                .filter(e -> items.contains(e.getItem()))
                .handler(e ->{
                    if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK){

                        if (!npcMap.containsKey(e.getPlayer().getUniqueId())){
                            npcMap.put(e.getPlayer().getUniqueId(),RecallHelper.createNpc(e.getPlayer()));
                            Schedulers.sync().runLater(() -> {
                                RecallHelper.removeNPCPacket(npcMap.get(e.getPlayer().getUniqueId()));
                                removedNPCS.add(npcMap.get(e.getPlayer().getUniqueId()));
                                npcMap.remove(e.getPlayer().getUniqueId(),npcMap.get(e.getPlayer().getUniqueId()));
                                teleportedPlayers.remove(e.getPlayer());

                            },200L); //Cufromstomizable MADAFAKA
                        }else e.getPlayer().sendMessage("You have already A NPC");

                    }else if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK){
                        if (npcMap.containsKey(e.getPlayer().getUniqueId())){
                            if (!teleportedPlayers.contains(e.getPlayer())){
                                UUID uuid = e.getPlayer().getUniqueId();
                                e.getPlayer().teleport(new Location(Bukkit.getWorld(npcMap.get(uuid).getWorld().getWorld().getName()),
                                        npcMap.get(uuid).locX(),
                                        npcMap.get(uuid).locY(),
                                        npcMap.get(uuid).locZ()));
                                teleportedPlayers.add(e.getPlayer());
                            }else
                                e.getPlayer().sendMessage("You teleported before !");
                        }else
                            e.getPlayer().sendMessage("You dont have NPC !");
                    }

                });

    }
}
