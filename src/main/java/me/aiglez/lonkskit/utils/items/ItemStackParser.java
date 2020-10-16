package me.aiglez.lonkskit.utils.items;

import me.aiglez.lonkskit.LonksKitProvider;
import me.aiglez.lonkskit.abilities.Ability;
import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.utils.Logger;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ItemStackParser {

    private static final Map<String, Color> COLORS = new HashMap<String, Color>() {{
        put("WHITE", Color.WHITE);
        put("SILVER", Color.SILVER);
        put("GRAY", Color.GRAY);
        put("BLACK", Color.BLACK);
        put("RED", Color.RED);
        put("MAROON", Color.MAROON);
        put("YELLOW", Color.YELLOW);
        put("OLIVE", Color.OLIVE);
        put("LIME", Color.LIME);
        put("GREEN", Color.GREEN);
        put("AQUA", Color.AQUA);
        put("TEAL", Color.TEAL);
        put("BLUE", Color.BLUE);
        put("NAVY", Color.NAVY);
        put("FUCHSIA", Color.FUCHSIA);
        put("PURPLE", Color.PURPLE);
        put("ORANGE", Color.ORANGE);
    }};

    public static Optional<ItemStack> parseByString(final String unparsed) {
        if(unparsed == null || unparsed.isEmpty()) return Optional.empty();
        if(unparsed.startsWith("{ability-") && unparsed.endsWith("}")) {
            final String abilityName = unparsed.replace("{ability-", "").replace("}", "");
            final Optional<Ability> ability = LonksKitProvider.getAbilityFactory().getAbility(abilityName);
            if(ability.isPresent()) {
                if(ability.get() instanceof ItemStackAbility) {
                    Logger.debug("Found ability item " + abilityName);
                    return Optional.of(((ItemStackAbility) ability.get()).getItemStack());
                }
                Logger.severe("The ability (" + abilityName + ") is not item based !");
            }

            return Optional.of(ItemStackBuilder.of(Material.NETHER_STAR)
                    .name("&7ABILITY: &b" + abilityName)
                    .build());
        }

        final String[] split = unparsed.split(", ");
        ItemStackBuilder builder = null;

        for (final String string : split) {

            if(StringUtils.startsWithIgnoreCase(string, "material:")) {
                final String materialName = StringUtils.replace(string, "material:", "");
                final Material material = Material.matchMaterial(materialName);
                if(material == null) {
                    throwParseError(unparsed, "Couldn't find any material with name '" + materialName + "', Skipping this item." );
                    return Optional.empty();
                }
                builder = ItemStackBuilder.of(material);

            } else if(StringUtils.startsWithIgnoreCase(string, "name:")) {
                final String name = StringUtils.replace(string, "name:", "");
                if (name == null || name.isEmpty() || builder == null) {
                    throwParseError(unparsed, "Either the BUILDER hasn't been instantiated yet (== Error in the material), or the name is not defined!");
                    return Optional.empty();
                }
                builder.name(name);

            } else if(StringUtils.startsWithIgnoreCase(string, "lore:")) {
                final String joinedLore = StringUtils.replace(string, "lore:", "");
                if (joinedLore == null || joinedLore.isEmpty() || builder == null) {
                    throwParseError(unparsed, "Either the BUILDER hasn't been instantiated yet (== Error in the material), or the lore is not defined!");
                    return Optional.empty();
                }
                final String[] splitLore = joinedLore.split("\\|");

                builder.transformMeta(meta -> {
                    meta.setLore(Arrays.asList(splitLore));
                });

            } else if(StringUtils.startsWithIgnoreCase(string, "data:")) {
                final int data = NumberUtils.toInt(StringUtils.replace(string, "data:", ""), -11);
                if (data == -11 || builder == null) {
                    throwParseError(unparsed, "Either the BUILDER hasn't been instantiated yet (== Error in the material), or the data is not defined or not valid (must be a number)");
                    return Optional.empty();
                }
                builder.data(data);

            } else if(StringUtils.startsWithIgnoreCase(string, "amount:")) {
                final int amnt = NumberUtils.toInt(StringUtils.replace(string, "amount:", ""), 1);
                if(amnt == -11 || builder == null) {
                    throwParseError(unparsed, "Either the BUILDER hasn't been instantiated yet (== Error in the material)");
                    return Optional.empty();
                }
                Logger.debug("Setting amount to > " + amnt);
                builder.amount(amnt);
                Logger.debug("New amount: " + builder.itemStack.getAmount());
            } else if(StringUtils.startsWithIgnoreCase(string, "enchant:")) {
                final String[] enchantSplit = StringUtils.replace(string, "enchant:", "").split(":");
                if(enchantSplit == null || enchantSplit.length < 2 || builder == null) {
                    throwParseError(unparsed, "Either the BUILDER hasn't been instantiated yet (== Error in the material), or the enchant format is not defined or not valid (Format: `enchant_name:level`)");
                    return Optional.empty();
                }

                final Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchantSplit[0].toLowerCase()));
                final int level = NumberUtils.toInt(enchantSplit[1], 1);
                if (enchantment == null) {
                    throwParseError(unparsed, "Couldn't find any enchantement with name `" + enchantSplit[0] + "`");
                    continue;
                }
                builder.enchant(enchantment, level);

            } else if(StringUtils.startsWithIgnoreCase(string, "color:")) {
                final String colorName = StringUtils.replace(string, "color:", "");
                if(colorName == null || colorName.isEmpty() || builder == null) {
                    throwParseError(unparsed, "Either the BUILDER hasn't been instantiated yet (== Error in the material), or the color name is not defined!");
                    return Optional.empty();
                }
                Optional<Color> color = getColorByName(colorName);
                if(color.isPresent()) {
                    builder.color(color.get());
                } else {
                    throwParseError(unparsed, "The color `" + colorName + "` is not valid!");
                }
            }
        }
        return builder == null ? Optional.empty() : Optional.of(builder.build());
    }

    private static void throwParseError(final String unparsed, final String error) {
        Logger.severe(error + "\n" + "Text to parse: " + unparsed);
    }

    public static Optional<Color> getColorByName(final String name) {
        if(name == null || name.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(COLORS.get(name.toUpperCase()));
    }

    private ItemStackParser() {
        throw new UnsupportedOperationException();
    }
}
