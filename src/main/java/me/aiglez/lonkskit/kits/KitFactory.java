package me.aiglez.lonkskit.kits;

import me.aiglez.lonkskit.kits.impl.KitFactoryImpl;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.stream.Collectors;

public interface KitFactory {

    Set<Kit> getRegisteredKits();

    SortedSet<Kit> getSortedKitsBySlots();

    Optional<Kit> getKit(String backendName);

    boolean tryRegisterKit(final Kit kit);

    boolean loadKits();

    default Set<Kit> getEnabledKits() {
        return Collections.unmodifiableSet(getRegisteredKits().stream().filter(Kit::enabled).collect(Collectors.toSet()));
    }

    static KitFactory make() {
        return new KitFactoryImpl();
    }
}
