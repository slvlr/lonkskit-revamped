package me.aiglez.lonkskit.kits.impl;

import com.google.common.base.Preconditions;
import me.aiglez.lonkskit.abilities.Ability;
import me.aiglez.lonkskit.kits.Kit;
import me.aiglez.lonkskit.kits.KitSelectorHolder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import scala.Int;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MemoryKit implements Kit {

    private final String backendName, displayName;
    private final Map<Integer,List<ItemStack>> inventoryContent;
    private final Map<Integer,Map<EquipmentSlot, ItemStack>> inventoryArmor;
    private final KitSelectorHolder selectorHolder;

    private final boolean rentable;
    private final int     rentCost;
    private int     usesPerRent;
    private final boolean isCustom;
    private final Set<PotionEffect> potionEffects;
    private final Set<Ability>      abilities;

    private boolean enabled;


    public MemoryKit(final String backendName, final String displayName, final Map<Integer, List<ItemStack>> inventoryContent,
                     final Map<Integer, Map<EquipmentSlot, ItemStack>> inventoryArmor, final KitSelectorHolder selectorHolder,
                     final boolean rentable, final int rentCost, final int usesPerCost, boolean isCustom, final Set<PotionEffect> potionEffects, final Set<Ability> abilities) {
        this.isCustom = isCustom;
        Preconditions.checkNotNull(backendName);
        Preconditions.checkNotNull(displayName);
        Preconditions.checkNotNull(inventoryContent);
        Preconditions.checkNotNull(inventoryArmor);
        Preconditions.checkNotNull(selectorHolder);
        this.backendName = backendName;
        this.displayName = displayName;
        this.inventoryArmor = inventoryArmor;
        this.inventoryContent = inventoryContent;
        this.selectorHolder = selectorHolder;
        this.rentable = rentable;
        this.rentCost = rentCost;
        this.usesPerRent = usesPerCost;
        this.potionEffects = (potionEffects == null ? Collections.emptySet() : potionEffects);
        this.abilities = (abilities == null ? Collections.emptySet() : abilities);
    }

    // -------------------------------------------- //
    // FUNCTIONS
    // -------------------------------------------- //
    @Override
    public boolean hasAbility(Ability ability) {
        return abilities.stream().anyMatch(f -> f.getName().equalsIgnoreCase(ability.getName()));
    }

    // -------------------------------------------- //
    // GETTERS & SETTERS
    // -------------------------------------------- //
    @Override
    public String getBackendName() {
        return this.backendName;
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public boolean enabled() {
        return this.enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public Set<PotionEffect> getPotionEffects() {
        return this.potionEffects;
    }

    @Override
    public Set<Ability> getAbilities() {
        return this.abilities;
    }

    @Override
    public KitSelectorHolder getSelectorHolder() {
        return this.selectorHolder;
    }

    @Override
    public boolean isRentable() {
        return this.rentable;
    }

    @Override
    public int getRentCost() {
        return this.rentCost;
    }

    @Override
    public int getUsesPerRent() {
        return this.usesPerRent;
    }

    @Override
    public List<ItemStack> getInventoryContent(final int level) { return Collections.unmodifiableList(this.inventoryContent.get(level)); }

    @Override
    public Map<EquipmentSlot, ItemStack> getInventoryArmors(final int level) { return this.inventoryArmor.get(level); }

    @Override
    public boolean isCustom() {
        return this.isCustom;
    }

    @Override
    public void incrementUses() { this.usesPerRent++; }

    @Override
    public void decrementUses() {
        this.usesPerRent--;
    }

    @Override
    public Map<Integer, List<ItemStack>> getInventories() {
        return this.inventoryContent;
    }
    @Override
    public Map<Integer,Map<EquipmentSlot, ItemStack>> getArmors() {
        return this.inventoryArmor;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("backendName", this.backendName)
                .toString();
    }
}
