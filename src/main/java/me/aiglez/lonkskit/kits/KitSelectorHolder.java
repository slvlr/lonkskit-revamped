package me.aiglez.lonkskit.kits;

import com.google.common.reflect.TypeToken;
import me.lucko.helper.config.ConfigurationNode;
import me.lucko.helper.config.objectmapping.ObjectMappingException;
import me.lucko.helper.item.ItemStackBuilder;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@SuppressWarnings("unused")
public class KitSelectorHolder {

    private final Material     material;
    private final String       displayName;
    private final boolean      display;
    private final int          slot;
    private final List<String> lore;

    public KitSelectorHolder(final Material material, final String displayName, final boolean display, final int slot, final List<String> lore) {
        this.material = material;
        this.displayName = displayName;
        this.display = display;
        this.slot = slot;
        this.lore = lore;
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
                            .build();
                } else {
                    return ItemStackBuilder.of(material)
                            .name(ChatColor.RED + displayName)
                            .lore(lore)
                            .lore("&7")
                            .lore("&7Access: &cNo Permission")
                            .lore("&cThis kit cannot be rented !")
                            .build();
                }

            case PERMANENT_ACCESS:
                return ItemStackBuilder.of(material)
                        .name(ChatColor.GREEN + displayName)
                        .lore(lore)
                        .lore("&7")
                        .lore("&7Access: &ePermanent")
                        .lore("&7Click to select this kit.")
                        .build();

            case RENTED:
                return ItemStackBuilder.of(material)
                        .name(ChatColor.GREEN + displayName)
                        .lore(lore)
                        .lore("&7")
                        .lore("&7Access: &aRented")
                        .lore("&7Click to select this kit.")
                        .lore("&7You have &b" + usesLeft + " &7use(s) left.")
                        .build();
        }
        // what else ??
        return null;
    }

    public static Builder builder(final ConfigurationNode node) {
        return new Builder(node);
    }

    public static class Builder {

        private final ConfigurationNode node;

        public Builder(final ConfigurationNode node) {
            this.node = node;
        }

        public KitSelectorHolder build() throws ObjectMappingException {
            return new KitSelectorHolder(Material.matchMaterial(node.getNode("material").getString("STONE")),
                    node.getNode("display-name").getString("A Selector Display name"),
                    node.getNode("display").getBoolean(true),
                    node.getNode("slot").getInt(RandomUtils.nextInt(10)),
                    node.getNode("lore").getList(new TypeToken<String>() {})
            );
        }

    }
}
