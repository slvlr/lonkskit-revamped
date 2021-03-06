package me.aiglez.lonkskit.abilities.helpers;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.aiglez.lonkskit.KitPlugin;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class RecallHelper {

    private static final List<EntityPlayer> NPC = new ArrayList<>();
    public static EntityPlayer createNpc(Player player){
        String[] skin = getSkin(player);
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer worldServer = ((CraftWorld) Objects.requireNonNull(Bukkit.getWorld(player.getWorld().getName()))).getHandle();
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(),player.getName());
        EntityPlayer npc = new EntityPlayer(server,worldServer,gameProfile,new PlayerInteractManager(worldServer));
        gameProfile.getProperties().put("textures",new Property("textures",skin[0],skin[1]));
        npc.setLocation(player.getLocation().getX(),
                player.getLocation().getY(),
                player.getLocation().getZ(),
                player.getLocation().getYaw(),
                player.getLocation().getPitch());
        npc.getBukkitEntity().getInventory().setHelmet(player.getInventory().getArmorContents()[3]);
        npc.getBukkitEntity().getInventory().setChestplate(player.getInventory().getArmorContents()[2]);
        npc.getBukkitEntity().getInventory().setLeggings(player.getInventory().getArmorContents()[1]);
        npc.getBukkitEntity().getInventory().setBoots(player.getInventory().getArmorContents()[0]);
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
        }
        Bukkit.getScheduler().runTaskLater(Helper.hostPlugin(),() -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
                connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER,npc));
            }
        },10L);
    }

    public static void addJoinPacket(Player player){
        for (EntityPlayer npc : NPC){
            PlayerConnection connection = ((CraftPlayer)player).getHandle().playerConnection;
            connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER,npc));
            connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
            connection.sendPacket(new PacketPlayOutEntityHeadRotation(npc, (byte) (npc.yaw * 256 / 360)));
        }
    }

    public static void removeNPCPacket(EntityPlayer npc) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
            connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER,npc));
            connection.sendPacket(new PacketPlayOutEntityDestroy(npc.getId()));
        }
    }
    private static String[] getSkin(Player player){
        EntityPlayer playerNMS = ((CraftPlayer) player).getHandle();
        GameProfile profile = playerNMS.getProfile();
        Property property = profile.getProperties().get("textures").iterator().next();
        String texture = property.getValue();
        String signature = property.getSignature();
        return new String[] {texture, signature};
    }


    public static List<EntityPlayer> getNPC() {
        return NPC;
    }
}
