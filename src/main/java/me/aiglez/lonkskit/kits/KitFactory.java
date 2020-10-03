package me.aiglez.lonkskit.kits;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public interface KitFactory {

    Set<Kit> getRegisteredKits();

    SortedSet<Kit> getSortedKitsBySlots();

    default Set<Kit> getEnabledKits() {
        return Collections.unmodifiableSet(getRegisteredKits().stream().filter(Kit::enabled).collect(Collectors.toSet()));
    }

    Optional<Kit> getKit(String backendName);

    boolean tryRegisterKit(final Kit kit);

    boolean loadKits();

    static KitFactory make() {
        return new KitFactoryImpl();
    }
}
