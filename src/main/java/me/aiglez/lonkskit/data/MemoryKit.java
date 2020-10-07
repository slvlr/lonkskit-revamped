package me.aiglez.lonkskit.data;

import com.google.common.base.Preconditions;
import me.aiglez.lonkskit.abilities.Ability;
import me.aiglez.lonkskit.kits.Kit;
import me.aiglez.lonkskit.kits.KitSelectorHolder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MemoryKit implements Kit {

    private final String backendName, displayName;
    private final List<ItemStack> inventoryContent;
    private final Map<EquipmentSlot, ItemStack> inventoryArmor;
    private final KitSelectorHolder selectorHolder;

    private final boolean rentable;
    private final int     rentCost;
    private final int     usesPerRent;

    private final Set<PotionEffect> potionEffects;
    private final Set<Ability>      abilities;

    private boolean enabled;


    public MemoryKit(final String backendName, final String displayName, final List<ItemStack> inventoryContent,
                     final Map<EquipmentSlot, ItemStack> inventoryArmor, final KitSelectorHolder selectorHolder,
                     final boolean rentable, final int rentCost, final int usesPerCost, final Set<PotionEffect> potionEffects, final Set<Ability> abilities) {

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
    public List<ItemStack> getInventoryContent() { return Collections.unmodifiableList(this.inventoryContent); }

    @Override
    public Map<EquipmentSlot, ItemStack> getInventoryArmors() { return Collections.unmodifiableMap(this.inventoryArmor); }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("backendName", this.backendName)
                .toString();
    }
}
