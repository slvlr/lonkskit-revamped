package me.aiglez.lonkskit.kits;

import com.google.common.collect.Lists;
import me.aiglez.lonkskit.abilities.Ability;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Kit {

    // -------------------------------------------- //
    // MAIN
    // -------------------------------------------- //
    String getBackendName();

    String getDisplayName();

    boolean enabled();

    void setEnabled(boolean enabled);

    Set<PotionEffect> getPotionEffects();

    Set<Ability> getAbilities();

    KitSelectorHolder getSelectorHolder();

    boolean hasAbility(Ability ability);
    // -------------------------------------------- //
    // Rents
    // -------------------------------------------- //
    boolean isRentable();

    int getRentCost();

    int getUsesPerRent();

    // -------------------------------------------- //
    // Inventory
    // -------------------------------------------- //
    List<ItemStack> getInventoryContent(final int level);

    Map<EquipmentSlot, ItemStack> getInventoryArmors(final int level);

    boolean isCustom();

    void incrementUses();

    void decrementUses();

    Map<Integer,List<ItemStack>> getInventories();

    Map<Integer,Map<EquipmentSlot, ItemStack>> getArmors();
}
