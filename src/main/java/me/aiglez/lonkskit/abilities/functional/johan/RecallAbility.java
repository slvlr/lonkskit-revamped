package me.aiglez.lonkskit.abilities.functional.johan;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.aiglez.lonkskit.abilities.FunctionalAbility;
import me.aiglez.lonkskit.abilities.helpers.RecallHelper;
import me.aiglez.lonkskit.events.KitSelectEvent;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.config.yaml.YAMLConfigurationLoader;
import me.lucko.helper.scheduler.Ticks;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class RecallAbility extends FunctionalAbility {

    private final Map<UUID, EntityPlayer> npcMap = Maps.newHashMap();
    private final List<Player> teleportedPlayers = Lists.newArrayList();
    private final List<Player> heads = Lists.newArrayList();
    private final List<ItemStack> items = Lists.newArrayList();
    public static final List<EntityPlayer> removedNPCS = Lists.newArrayList();

    public RecallAbility(YAMLConfigurationLoader configurationLoader) throws IOException {
        super("recall", configurationLoader);
    }

    @Override
    public void registerListeners() {
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
                .filter(e ->{
                    String backendName = e.getKit().getBackendName().toUpperCase();
                    if (backendName.contains("RECALL")){
                        return true;
                    }else{
                        if (heads.contains(e.getLocalPlayer().toBukkit())) heads.remove(e.getLocalPlayer().toBukkit());
                    }
                    return false;
                })
                .handler(e ->{
                    applyEffects(e.getLocalPlayer());
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

                            }, Ticks.from(configuration.getNode("disappear-delay").getLong(10L), TimeUnit.SECONDS));
                        } else {
                            LocalPlayer.get(e.getPlayer()).msg(configuration.getNode("messages", "already-have-npc"));
                        }

                    }else if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK){
                        if (npcMap.containsKey(e.getPlayer().getUniqueId())){
                            if (!teleportedPlayers.contains(e.getPlayer())){
                                UUID uuid = e.getPlayer().getUniqueId();
                                e.getPlayer().teleport(new Location(Bukkit.getWorld(npcMap.get(uuid).getWorld().getWorld().getName()),
                                        npcMap.get(uuid).locX(),
                                        npcMap.get(uuid).locY(),
                                        npcMap.get(uuid).locZ()));
                                teleportedPlayers.add(e.getPlayer());
                            } else {
                                LocalPlayer.get(e.getPlayer()).msg(configuration.getNode("messages", "teleported-before"));
                            }
                        } else {
                            LocalPlayer.get(e.getPlayer()).msg(configuration.getNode("messages", "no-npc"));
                        }
                    }

                });

    }
}
