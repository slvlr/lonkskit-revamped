package me.aiglez.lonkskit.abilities.external.itembased.helpers;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_16_R2.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R2.CraftServer;
import org.bukkit.craftbukkit.v1_16_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RecallHelper {
    private static List<EntityPlayer> NPC = new ArrayList<EntityPlayer>();
    public static EntityPlayer createNpc(Player player){
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer worldServer = ((CraftWorld) Bukkit.getWorld(player.getWorld().getName())).getHandle();
        GameProfile profile = new GameProfile(UUID.randomUUID(),player.getName());
        EntityPlayer npc = new EntityPlayer(server,worldServer,profile,new PlayerInteractManager(worldServer));
        npc.setLocation(player.getLocation().getX(),player.getLocation().getY(),player.getLocation().getZ(),player.getLocation().getYaw(),player.getLocation().getPitch());
        String[] name = setSkin(player);
        profile.getProperties().put("textures",new Property("textures",name[0],name[1]));
        addNPCPacket(npc);
        NPC.add(npc);
        return npc;
    }

    public static void addNPCPacket(EntityPlayer npc){
        for (Player player : Bukkit.getOnlinePlayers()){
            PlayerConnection connection = ((CraftPlayer)player).getHandle().playerConnection;
            connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER,npc));
            connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
            connection.sendPacket(new PacketPlayOutEntityHeadRotation(npc, (byte) (npc.yaw * 256 / 360)));
            connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER,npc));
        }
    }

    public static void addJoinPacket(Player player){
        for (EntityPlayer npc : NPC){
            PlayerConnection connection = ((CraftPlayer)player).getHandle().playerConnection;
            connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER,npc));
            connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
            connection.sendPacket(new PacketPlayOutEntityHeadRotation(npc, (byte) (npc.yaw * 256 / 360)));
            connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER,npc));
        }
    }

    public static void removeNPCPacket(Entity npc) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
            connection.sendPacket(new PacketPlayOutEntityDestroy(npc.getId()));
        }
    }
    private static String[] setSkin(Player player){
        EntityPlayer p = ((CraftPlayer)player).getHandle();
        GameProfile profile = p.getProfile();
        Property property = profile.getProperties().get("textures").iterator().next();
        String textures = property.getValue();
        String signature = property.getSignature();
        return new String[] {textures,signature};
    }

    public static List<EntityPlayer> getNPC() {
        return NPC;
    }
}
