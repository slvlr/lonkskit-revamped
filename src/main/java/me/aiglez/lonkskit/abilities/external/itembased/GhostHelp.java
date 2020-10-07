package me.aiglez.lonkskit.abilities.external.itembased;

import com.comphenix.protocol.Packets;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ConnectionSide;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.flowpowered.math.vector.Vector3d;
import me.aiglez.lonkskit.KitPlugin;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static co.aikar.commands.ACFUtil.hasIntersection;

public class GhostHelp {
    ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();



    public GhostHelp(Player hit) {
        protocolManager.getAsynchronousManager().registerAsyncHandler(
                new PacketAdapter(KitPlugin.getSingleton(), ConnectionSide.CLIENT_SIDE, Packets.Server.ARM_ANIMATION) {

                    @Override
                    public void onPacketReceiving(PacketEvent event) {
                        final int ATTACK_REACH = 4;
                        Random rnd = new Random();

                        Player observer = hit;
                        Location observerPos = observer.getEyeLocation();
                        Vector observerDir = new Vector(observerPos.getDirection().getX(),observerPos.getDirection().getY(),observerPos.getDirection().getZ());
                        Vector observerStart = new Vector(observerPos.getX(),observerPos.getY(),observerPos.getZ());
                        Vector observerEnd = observerStart.add(observerDir.multiply(ATTACK_REACH));

                        Player hit = null;

                        // Get nearby entities
                        for (Player target : protocolManager.getEntityTrackers(observer)) {
                            // No need to simulate an attack if the player is already visible
                            if (!observer.canSee(target)) {
                                // Bounding box of the given player
                                Vector targetPos = new Vector(target.getLocation().getX(),target.getLocation().getY(),target.getLocation().getZ());
                                Vector minimum = targetPos.add(new Vector(-0.5, 0, -0.5));
                                Vector maximum = targetPos.add(new Vector(0.5, 1.67, 0.5));
                                List<Vector> list1 = new ArrayList<Vector>(),list2 = new ArrayList<Vector>();
                                list1.add(observerStart);
                                list1.add(minimum);
                                list2.add(observerEnd);
                                list2.add(maximum);
                                if (hasIntersection(list1,list2)) {//observerStart, observerEnd, minimum, maximum
                                    if (hit == null || hit.getLocation().distanceSquared(observerPos) > target.getLocation().distanceSquared(observerPos)) {
                                        hit = target;
                                    }
                                }
                            }
                        }

                        // Simulate a hit against the closest player
                        if (hit != null) {
                            PacketContainer useEntity = protocolManager.createPacket(Packets.Client.USE_ENTITY, false);
                            useEntity.getIntegers().
                                    write(0, observer.getEntityId()).
                                    write(1, hit.getEntityId()).
                                    write(2, 1 /* LEFT_CLICK */);

                            // Chance of breaking the visibility

                            try {
                                protocolManager.recieveClientPacket(event.getPlayer(), useEntity);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    // Get entity trackers is not thread safe
                }).syncStart();
    }
}

