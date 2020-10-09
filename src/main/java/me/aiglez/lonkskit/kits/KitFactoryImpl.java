package me.aiglez.lonkskit.kits;

import com.google.common.primitives.Ints;
import com.google.common.reflect.TypeToken;
import me.aiglez.lonkskit.KitPlugin;
import me.aiglez.lonkskit.LonksKitProvider;
import me.aiglez.lonkskit.abilities.Ability;
import me.aiglez.lonkskit.data.MemoryKit;
import me.aiglez.lonkskit.utils.Logger;
import me.aiglez.lonkskit.utils.items.ItemStackParser;
import me.lucko.helper.config.ConfigFactory;
import me.lucko.helper.config.ConfigurationNode;
import me.lucko.helper.config.objectmapping.ObjectMappingException;
import me.lucko.helper.function.chain.Chain;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class KitFactoryImpl implements KitFactory {

    private final Set<Kit> kits;
    private final SortedSet<Kit> orderedKits;

    public KitFactoryImpl() {
        this.kits = new HashSet<>();
        this.orderedKits = new TreeSet<>((o1, o2) -> Ints.compare(o1.getSelectorHolder().slot(), o2.getSelectorHolder().slot()));
    }

    @Override
    public Optional<Kit> getKit(String backendName) {
        for (final Kit kit : kits) {
            if(kit.getBackendName().equalsIgnoreCase(backendName)) return Optional.of(kit);
        }
        return Optional.empty();
    }

    @Override
    public Set<Kit> getRegisteredKits() {
        return Collections.unmodifiableSet(kits);
    }

    @Override
    public SortedSet<Kit> getSortedKitsBySlots() {
        return Collections.unmodifiableSortedSet(orderedKits);
    }

    @Override
    public boolean tryRegisterKit(Kit kit) {
        if(kit == null) return false;
        if(kit.getBackendName() != null && kit.getDisplayName() != null && kit.getSelectorHolder() != null
                && kit.getInventoryArmors() != null && kit.getInventoryContent() != null) {
            return kits.add(kit);
        }
        return false;
    }

    @Override
    public boolean loadKits() {
        final File kitsDir = new File(KitPlugin.getSingleton().getDataFolder() + File.separator + "kits");
        final File[] files = kitsDir.listFiles((dir, name) -> name.endsWith(".yml"));
        if(files == null || files.length == 0) {
            Logger.warn("No file found in kits/ directory !");
            return true;
        }

        for (int i = 0; i < files.length; i++) {
            final File file = files[i];
            if(file == null) {
                Logger.severe("A weird error occurred: Couldn't get the file at index (" + i + ") Length (" + files.length + ")");
                return false;
            }

            final ConfigurationNode node = ConfigFactory.yaml().load(file);
            boolean registerResult = tryRegisterKit(getKitByNode(node).get());
            if(!registerResult) {
                Logger.warn("Couldn't load kit at " + file.getName());
            }
        }

        Logger.debug("Ordering kits by slots...");
        for (final Kit kit : kits) {
            if(!kit.enabled()) {
                Logger.debug("> " + kit.getBackendName() + " : disabled skipping it");
                continue;
            }

            if(!kit.getSelectorHolder().display()) {
                Logger.debug("> " + kit.getBackendName() + " : 'display' is false, skipping it");
                continue;
            }

            Logger.debug("> " + kit.getBackendName() + " | slot: " + kit.getSelectorHolder().slot());
            orderedKits.add(kit);
        }
        Logger.fine("Loaded " + kits.size() + " kit(s) (" + (kits.size() - getEnabledKits().size()) + " disabled)");
        return true;
    }

    // Utility methods //

    public Optional<Kit> getKitByNode(final ConfigurationNode node) {
        final String displayName = node.getNode("display-name").getString("A display name");
        final String backendName = displayName.replaceAll(" ", "-")
                .replaceAll("§", "")
                .replaceAll("&", "").toLowerCase();

        final boolean enabled = node.getNode("enabled").getBoolean(true);

        List<ItemStack> inventoryContent;
        Map<EquipmentSlot, ItemStack> inventoryArmor;
        try {
            inventoryContent = Chain.start(node.getNode("inventory", "content").getList(new TypeToken<String>() {}))
                    .map(list -> list.stream().map(ItemStackParser::parseByString).collect(Collectors.toList()))
                    .map(optionals -> optionals.stream().filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList()))
                    .orElseIfNull(Collections.emptyList()).endOrNull();

            inventoryArmor = Chain.start(node.getNode("inventory", "armor").getValue(new TypeToken<Map<EquipmentSlot, String>>() {}))
                    .map(map -> {
                        Map<EquipmentSlot, ItemStack> rt = new HashMap<>();
                        map.forEach((equipmentSlot, unparsed) -> {
                            final Optional<ItemStack> parsed = ItemStackParser.parseByString(unparsed);
                            parsed.ifPresent(itemStack -> rt.put(equipmentSlot, itemStack));
                        });
                        return rt;
                    })
                    .orElseIfNull(Collections.emptyMap()).endOrNull();
        } catch (ObjectMappingException e) {
            Logger.severe("Couldn't map an object (LIST) // " + e.getMessage());
            inventoryContent = Collections.emptyList();
            inventoryArmor = Collections.emptyMap();
        }

        final boolean rentable = node.getNode("rent", "rentable").getBoolean(false);
        final int rentCost = node.getNode("rent", "cost").getInt(0);
        final int usesPerRent = node.getNode("rent", "uses").getInt(0);

        Set<PotionEffect> potionEffects;
        try {
            potionEffects = Chain.start(node.getNode("potion-effects").getList(new TypeToken<String>() {}))
                    .map(list -> list.stream().map(this::tryParsingPotionEffect).collect(Collectors.toSet()))
                    .map(optionals -> optionals.stream().filter(Optional::isPresent).map(Optional::get).collect(Collectors.toSet()))
                    .orElseIfNull(Collections.emptySet()).endOrNull();

        } catch (ObjectMappingException e) {
            Logger.severe("Couldn't map an object (LIST) // " + e.getMessage());
            potionEffects = null;
        }

        Set<Ability> abilities;
        try {
            abilities = Chain.start(node.getNode("abilities").getList(new TypeToken<String>() {}))
                    .map(list -> list.stream().map(ability -> {
                        return LonksKitProvider.getAbilityFactory().getAbility(ability);
                    }).collect(Collectors.toSet()))
                    .map(optionals -> optionals.stream().filter(Optional::isPresent).map(Optional::get).collect(Collectors.toSet()))
                    .orElseIfNull(Collections.emptySet()).endOrNull();

        } catch (ObjectMappingException e) {
            Logger.severe("Couldn't map an object (LIST) // " + e.getMessage());
            abilities = null;
        }

        KitSelectorHolder kitSelectorHolder;
        try {
            kitSelectorHolder = KitSelectorHolder.builder(node.getNode("selector")).build();
        } catch (ObjectMappingException e) {
            Logger.severe("Couldn't map an object (LIST) (Kit Selector) // " + e.getMessage());
            return Optional.empty();
        }

        final Kit newKit = new MemoryKit(
                backendName,
                displayName,
                inventoryContent,
                inventoryArmor,
                kitSelectorHolder,
                rentable,
                rentCost,
                usesPerRent,
                potionEffects,
                abilities
        );

        if(newKit != null) newKit.setEnabled(enabled);
        return Optional.of(newKit);
    }

    private Optional<PotionEffect> tryParsingPotionEffect(String from) {
        if(from == null || from.isEmpty()) return Optional.empty();
        final String[] split = from.split(":");
        if(split == null || split.length < 2) {
            return Optional.empty();
        }
        final PotionEffectType type = PotionEffectType.getByName(split[0]);
        if(type == null) {
            System.out.println("Potion effect type not found: " + split[0]);
            return Optional.empty();
        }
        return Optional.of(new PotionEffect(type, Integer.MAX_VALUE, NumberUtils.toInt(split[1], 0) + 1));
    }
}
