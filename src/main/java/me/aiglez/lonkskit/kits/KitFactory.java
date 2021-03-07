package me.aiglez.lonkskit.kits;

import me.aiglez.lonkskit.guis.KitSelectorGUI;
import me.aiglez.lonkskit.kits.impl.KitFactoryImpl;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public interface KitFactory {

    Set<Kit> getRegisteredKits();

    SortedSet<Kit> getSortedKitsBySlots();


    Optional<Kit> getKit(String backendName);

    boolean tryRegisterKit(final Kit kit);

    void loadKits();

    default Set<Kit> getEnabledKits() {
        return Collections.unmodifiableSet(getRegisteredKits().stream().filter(Kit::enabled).collect(Collectors.toSet()));
    }
    default List<Kit> getCustomKits(){
        return Collections.unmodifiableList(getRegisteredKits().stream().filter(Kit::isCustom).collect(Collectors.toList()));
    }
    default Kit getKitByItem(ItemStack item){
        return getCustomKits().stream().filter(kit ->kit.getSelectorHolder().buildItem(kit, KitSelectorGUI.State.PERMANENT_ACCESS,kit.getUsesPerRent()).getType() == item.getType()).findAny().get();
    }

    static KitFactory make() {
        return new KitFactoryImpl();
    }
}
