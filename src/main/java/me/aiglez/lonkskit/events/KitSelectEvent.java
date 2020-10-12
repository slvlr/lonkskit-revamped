package me.aiglez.lonkskit.events;

import com.google.common.base.Preconditions;

import me.aiglez.lonkskit.kits.Kit;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.lucko.helper.Events;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;


public class KitSelectEvent extends KitEvent {

    private static final HandlerList HANDLERS = new HandlerList();
    private final LocalPlayer localPlayer;

    public KitSelectEvent(Kit kit, LocalPlayer localPlayer) {
        super(kit);
        Preconditions.checkNotNull(kit);
        this.localPlayer = localPlayer;
        if (kit.getBackendName().equalsIgnoreCase("disguise-pig")){
        }
        if (kit.getBackendName().equalsIgnoreCase("disguise-spider")){
            Events.subscribe(PlayerMoveEvent.class)
                    .filter(e -> LocalPlayer.get(e.getPlayer()).getNullableSelectedKit() == kit)
                    .handler(e -> {
                        Block block = e.getTo().getBlock();
                        Vector normal = localPlayer.toBukkit().getVelocity();
                        if (block.getType() == Material.COBWEB){
                            localPlayer.toBukkit().setVelocity(normal.multiply(2));

                        }else
                            localPlayer.toBukkit().setVelocity(normal);

                    });
        }



    }



    public LocalPlayer getLocalPlayer() {
        return localPlayer;
    }

    @Override
    public HandlerList getHandlers() { return HANDLERS; }
}
