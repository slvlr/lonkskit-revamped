package me.aiglez.lonkskit.kits.impl;

import com.google.common.collect.Maps;
import com.google.common.primitives.Ints;
import com.google.common.reflect.TypeToken;
import me.aiglez.lonkskit.KitPlugin;
import me.aiglez.lonkskit.LonksKitProvider;
import me.aiglez.lonkskit.abilities.Ability;
import me.aiglez.lonkskit.kits.Kit;
import me.aiglez.lonkskit.kits.KitFactory;
import me.aiglez.lonkskit.kits.KitSelectorHolder;
import me.aiglez.lonkskit.utils.Logger;
import me.aiglez.lonkskit.utils.PotionEffectBuilder;
import me.aiglez.lonkskit.utils.items.ItemStackParser;
import me.lucko.helper.config.ConfigFactory;
import me.lucko.helper.config.ConfigurationNode;
import me.lucko.helper.config.objectmapping.ObjectMappingException;
import me.lucko.helper.function.chain.Chain;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class KitFactoryImpl implements KitFactory {

    private final Set<Kit> kits;
    private final SortedSet<Kit> orderedKits;
    private static final Map<Kit,List<String>> lores = Maps.newHashMap();
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
                && kit.getArmors()!= null && kit.getInventories() != null) {
            return kits.add(kit);
        }
        return false;
    }

    /*
     * This code is complete mess, needs a refactor
     */
    @Override
    public void loadKits() {
        long ms = System.currentTimeMillis();
        final File kitsDir = new File(KitPlugin.getSingleton().getDataFolder() + File.separator + "kits");
        final File[] files = kitsDir.listFiles((dir, name) -> name.endsWith(".yml"));
        if(files == null || files.length == 0) {
            Logger.warn("No file found in kits/ directory !");
            return;
        }

        for (int i = 0; i < files.length; i++) {
            final File file = files[i];
            if(file == null) {
                Logger.severe("A weird error occurred: Couldn't get the file at index (" + i + ") Length (" + files.length + ")");
                return;
            }

            final ConfigurationNode node = ConfigFactory.yaml().load(file);
            boolean registerResult = tryRegisterKit(getKitByNode(node).get());
            if(!registerResult) {
                Logger.warn("Couldn't load kit at " + file.getName());
            }
        }

        for (final Kit kit : kits) {
            if(!kit.enabled() || !kit.getSelectorHolder().display()) {
                continue;
            }
            orderedKits.add(kit);
        }
        Logger.fine("Loaded " + kits.size() + " kit(s) (" + (kits.size() - getEnabledKits().size()) + " disabled)");
        Logger.fine("Loading took {0} ms", System.currentTimeMillis() - ms);
    }

    // Utility methods //

    public Optional<Kit> getKitByNode(final ConfigurationNode node) {
        final String displayName = node.getNode("display-name").getString("A display name");
        final String backendName = displayName.replaceAll(" ", "-")
                .replaceAll("§", "")
                .replaceAll("&", "").toLowerCase();

        final boolean enabled = node.getNode("enabled").getBoolean(true);

        final Map<Integer,List<ItemStack>> inventoryContents = Maps.newHashMap();
        final Map<Integer,Map<EquipmentSlot, ItemStack>> inventoryArmors = Maps.newHashMap();
        try {
            for (int i = 1; i <= node.getNode("upgrades").getChildrenMap().size() ; i++){
                try {
                    final ConfigurationNode inventoryNode = node.getNode("upgrades","level" + i , "inventory","content");

                    inventoryContents.put(i,inventoryNode.getList(TypeToken.of(String.class)).stream()
                            .map(ItemStackParser::parseByString).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList()));

                    inventoryArmors.put(i,Chain.start(node.getNode("upgrades","level" + i ,"armor").getValue(new TypeToken<Map<EquipmentSlot, String>>() {}))
                            .map(map -> {
                                Map<EquipmentSlot, ItemStack> rt = new HashMap<>();
                                map.forEach((equipmentSlot, unparsed) -> {
                                    final Optional<ItemStack> parsed = ItemStackParser.parseByString(unparsed);
                                    parsed.ifPresent(itemStack -> rt.put(equipmentSlot, itemStack));
                                });
                                return rt;
                            })
                            .orElseIfNull(Collections.emptyMap()).endOrNull());
                }catch (ArrayIndexOutOfBoundsException | NullPointerException ignored){}
            }
        } catch (ObjectMappingException e) {
            Logger.severe("Couldn't map an object (LIST) // " + e.getMessage());
            inventoryContents.clear();
            inventoryArmors.clear();
        }

        final boolean rentable = node.getNode("rent", "rentable").getBoolean(false);
        final int rentCost = node.getNode("rent", "cost").getInt(0);
        final int usesPerRent = node.getNode("rent", "uses").getInt(0);
        final boolean isCustom = node.getNode("isCustom").getBoolean();
        final double price = node.getNode("price").getDouble(0);
        Set<PotionEffect> potionEffects = null;

        try {
            potionEffects = Chain.start(node.getNode("potion-effects").getList(new TypeToken<String>() {}))
                    .map(list -> list.stream().map(unparsed -> PotionEffectBuilder.parse(unparsed).build()).filter(Objects::nonNull).collect(Collectors.toSet()))
                    .orElseIfNull(Collections.emptySet()).endOrNull();

        } catch (ObjectMappingException e) {
            Logger.severe("Couldn't map an object (LIST) // " + e.getMessage());
        }

        Set<Ability> abilities = null;
        try {
            abilities = Chain.start(node.getNode("abilities").getList(new TypeToken<String>() {}))
                    .map(list -> list.stream().map(ability -> LonksKitProvider.getAbilityFactory().getAbility(ability)).collect(Collectors.toSet()))
                    .map(optionals -> optionals.stream().filter(Optional::isPresent).map(Optional::get).collect(Collectors.toSet()))
                    .orElseIfNull(Collections.emptySet()).endOrNull();

        } catch (ObjectMappingException e) {
            Logger.severe("Couldn't map an object (LIST) // " + e.getMessage());
        }

        KitSelectorHolder kitSelectorHolder;
        try {
            kitSelectorHolder = KitSelectorHolder.builder(node).build();
        } catch (ObjectMappingException e) {
            Logger.severe("Couldn't map an object (LIST) (Kit Selector) // " + e.getMessage());
            return Optional.empty();
        }

        final Kit newKit = new MemoryKit(
                backendName,
                displayName,
                inventoryContents,
                inventoryArmors,
                kitSelectorHolder,
                rentable,
                rentCost,
                usesPerRent,
                isCustom, potionEffects,
                abilities, price
        );
        final List<String> lore = new LinkedList<>();
        for (int i = 1; i <= node.getNode("upgrades").getChildrenMap().size() ; i++){
            try {
                lore.addAll(
                        node.getNode("upgrades","level" + i,"lore").getList(TypeToken.of(String.class))
                );
            } catch (ObjectMappingException e) {
                KitPlugin.getSingleton().getLogger().warning("Can't read lore list of " + newKit.getDisplayName());
            }
        }

        if(newKit != null) newKit.setEnabled(enabled);
        return Optional.of(newKit);
    }

    public static Map<Kit, List<String>> getLores() {
        return lores;
    }
}
