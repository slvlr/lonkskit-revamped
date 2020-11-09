package me.aiglez.lonkskit.utils.items;

import com.google.common.base.Preconditions;
import me.aiglez.lonkskit.utils.Logger;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.UUID;

public class ItemStackNBT {

    public static ItemStack setKeyValue(ItemStack item, String key, Object value) {
        Preconditions.checkNotNull(item, "item may not be null");
        Preconditions.checkNotNull(key, "nbt key may not be null");
        Preconditions.checkNotNull(value, "value may not be null");
        final net.minecraft.server.v1_16_R3.ItemStack nmsCopy = CraftItemStack.asNMSCopy(item);
        if(value instanceof String) {
            nmsCopy.getOrCreateTag().set(key, net.minecraft.server.v1_16_R3.NBTTagString.a((String) value));
        } else if(value instanceof UUID) {
            nmsCopy.getOrCreateTag().set(key, net.minecraft.server.v1_16_R3.NBTTagString.a(((UUID) value).toString()));
        } else if(value instanceof Integer) {
            nmsCopy.getOrCreateTag().set(key, net.minecraft.server.v1_16_R3.NBTTagInt.a((int) value));
        } else if(value instanceof Byte) {
            nmsCopy.getOrCreateTag().set(key, net.minecraft.server.v1_16_R3.NBTTagByte.a((byte) value));
        } else {
            Logger.warn("The type in class {0} isn't supported yet!", value.getClass());
        }
        return CraftItemStack.asBukkitCopy(nmsCopy);
    }

    public static boolean hasKey(ItemStack item, String key) {
        Preconditions.checkNotNull(item, "item may not be null");
        Preconditions.checkNotNull(key, "nbt key may not be null");
        final net.minecraft.server.v1_16_R3.ItemStack nmsCopy = CraftItemStack.asNMSCopy(item);
        if(nmsCopy.getTag() == null) return false;
        return nmsCopy.getTag().hasKey(key);
    }

    public static Optional<String> getStringValue(ItemStack item, String key) {
        Preconditions.checkNotNull(item, "item may not be null");
        Preconditions.checkNotNull(key, "nbt key may not be null");
        final net.minecraft.server.v1_16_R3.ItemStack nmsCopy = CraftItemStack.asNMSCopy(item);
        if(nmsCopy.getTag() == null) return Optional.empty();
        return Optional.of(nmsCopy.getTag().getString(key));
    }

    public static Optional<UUID> getUUIDValue(ItemStack item, String key) {
        Preconditions.checkNotNull(item, "item may not be null");
        Preconditions.checkNotNull(key, "nbt key may not be null");
        final Optional<String> optionalUUID = getStringValue(item, key);
        return optionalUUID.map(UUID::fromString);
    }

}
