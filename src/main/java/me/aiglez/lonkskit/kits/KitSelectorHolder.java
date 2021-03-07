package me.aiglez.lonkskit.kits;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import me.aiglez.lonkskit.guis.KitSelectorGUI;
import me.aiglez.lonkskit.kits.impl.KitFactoryImpl;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.items.ItemStackBuilder;
import me.aiglez.lonkskit.utils.items.ItemStackParser;
import me.lucko.helper.config.ConfigurationNode;
import me.lucko.helper.config.objectmapping.ObjectMappingException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import scala.Int;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class KitSelectorHolder {

    private final Material     material;
    private final String       displayName;
    private final boolean      display;
    private final int          slot;
    private final List<String> lore;
    private Color              color;
    private final Map<Integer,List<String>> loreOfLevel;

    public KitSelectorHolder(final Material material, final String displayName, final boolean display, final int slot, final List<String> lore, Optional<Color> color, final Map<Integer,List<String>> loreOfLevel) {
        this.material = material;
        this.displayName = displayName;
        this.display = display;
        this.slot = slot;
        this.lore = lore;
        this.loreOfLevel = loreOfLevel;
        color.ifPresent(value -> this.color = value);
    }

    public boolean display() { return display; }

    public int slot() { return slot; }

    public ItemStack buildItem(Kit kit, KitSelectorGUI.State state, int usesLeft) {
        switch (state) {
            case NO_ACCESS:
                if(kit.isRentable()) {
                    return ItemStackBuilder.of(material)
                            .name(ChatColor.RED + displayName)
                            .lore(lore)
                            .lore("&7")
                            .lore("&7Access: §cNo Permission")
                            .lore("&7Click to rent this kit for &b" + kit.getRentCost() + " &7point(s), with &b" + kit.getUsesPerRent() + " &7use(s).")
                            .color(color)
                            .build();
                } else {
                    return ItemStackBuilder.of(material)
                            .name(ChatColor.RED + displayName)
                            .lore(lore)
                            .lore("&7")
                            .lore("&7Access: &cNo Permission")
                            .lore("&cThis kit cannot be rented !")
                            .color(color)
                            .build();
                }

            case PERMANENT_ACCESS:
                return ItemStackBuilder.of(material)
                        .name(ChatColor.GREEN + displayName)
                        .lore(lore)
                        .lore("&7")
                        .lore("&7Access: &ePermanent")
                        .lore("&7Click to select this kit.")
                        .color(color)
                        .build();

            case RENTED:
                return ItemStackBuilder.of(material)
                        .name(ChatColor.GREEN + displayName)
                        .lore(lore)
                        .lore("&7")
                        .lore("&7Access: &aRented")
                        .lore("&7Click to select this kit.")
                        .lore("&7You have &b" + usesLeft + " &7use(s) left.")
                        .color(color)
                        .build();
        }
        // what else ??
        return null;
    }

    public static Builder builder(final ConfigurationNode node) {
        return new Builder(node);
    }

    public ItemStack buildItemR(int level) {
        Preconditions.checkArgument(loreOfLevel.get(level) == null,"That is the max level");
        List<String > lore1 = loreOfLevel.get(level);

        return ItemStackBuilder.of(this.material)
                .name(ChatColor.GOLD + this.displayName)
                .lore(lore1)
                .color(this.color)
                .build();

    }
    public static class Builder {

        private ConfigurationNode node;

        public Builder(final ConfigurationNode node) {
            this.node = node;
        }

        public KitSelectorHolder build() throws ObjectMappingException {
            Map<Integer,List<String>> map = Maps.newHashMap();
            for (int i = 1; i <= this.node.getNode("upgrades").getChildrenMap().size(); i++){
                map.put(i,node.getNode("upgrades","level" + i, "lore").getList(TypeToken.of(String.class)));
                System.out.println("ok" + node.getNode("upgrades","level" + i, "lore").getValueType());
            }
            node = node.getNode("selector");
            return new KitSelectorHolder(Material.matchMaterial(node.getNode("material").getString("STONE")),
                    node.getNode("display-name").getString("A Selector Display name"),
                    node.getNode("display").getBoolean(true),
                    node.getNode("slot").getInt(RandomUtils.nextInt(10)),
                    node.getNode("lore").getList(new TypeToken<String>() {}),
                    ItemStackParser.getColorByName(node.getNode("color").getString()),
                    map
            );
        }

    }

    public Map<Integer, List<String>> getLoreOfLevel() {
        return loreOfLevel;
    }
}
