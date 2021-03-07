package me.aiglez.lonkskit.guis;

import me.aiglez.lonkskit.LonksKitProvider;
import me.aiglez.lonkskit.kits.KitSelectorHolder;
import me.aiglez.lonkskit.kits.impl.KitFactoryImpl;
import me.aiglez.lonkskit.messages.Messages;
import me.aiglez.lonkskit.messages.Replaceable;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.players.LocalRent;
import me.aiglez.lonkskit.utils.Logger;
import me.aiglez.lonkskit.utils.items.ItemStackBuilder;
import me.lucko.helper.menu.Item;
import me.lucko.helper.menu.paginated.PaginatedGui;
import me.lucko.helper.menu.paginated.PaginatedGuiBuilder;
import me.lucko.helper.menu.scheme.MenuScheme;
import me.lucko.helper.menu.scheme.StandardSchemeMappings;
import org.bukkit.Material;

import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ShopGUI extends PaginatedGui {
    private static final PaginatedGuiBuilder SETTINGS;

    static {
        final MenuScheme scheme = new MenuScheme(StandardSchemeMappings.HARDENED_CLAY);
        final List<Integer> itemSlots = new MenuScheme()
                .mask("111111111")
                .mask("111111111")
                .mask("111111111")
                .mask("111111111")
                .mask("111111111")
                .getMaskedIndexesImmutable();
        final int nextPageSlot = new MenuScheme()
                .maskEmpty(5)
                .mask("000000001")
                .getMaskedIndexes().get(0);

        final int previousPageSlot = new MenuScheme()
                .maskEmpty(5)
                .mask("100000000")
                .getMaskedIndexes().get(0);

        SETTINGS = PaginatedGuiBuilder.create().title("&lRank up Selector")
                .itemSlots(itemSlots)
                .scheme(scheme)
                .nextPageSlot(nextPageSlot).previousPageSlot(previousPageSlot);
    }
    public ShopGUI(LocalPlayer localPlayer) {
        super(getContent(localPlayer), localPlayer.toBukkit(), SETTINGS);
    }
    private static Function<PaginatedGui,List<Item>> getContent(LocalPlayer localPlayer) {
        return gui -> LonksKitProvider.getKitFactory().getEnabledKits().stream()
                .map(kit -> {
                    KitSelectorHolder holder = kit.getSelectorHolder();
                    if (!holder.display()) {
                        Logger.severe("A weird error occurred (kit selector holder not found for kit " + kit.getBackendName() + ")");
                        return ItemStackBuilder.of(Material.ITEM_FRAME).build(() -> localPlayer.msg("Hi"));
                    }
                    return ItemStackBuilder
                            .of(holder.buildItemR(localPlayer.getRank(kit).get().getLevel()))
                            .build(() -> {
                                if (localPlayer.getRank(kit).get().getLevel() >= kit.getSelectorHolder().getLoreOfLevel().values().size()){
                                    localPlayer.msg(Replaceable.handle(Messages.PLAYER_SELECT_MAX_LEVEL.getValue(),kit.getDisplayName()));
                                }
                                final boolean transaction = localPlayer.decrementPoints((int) kit.getPrice());
                                if (!transaction) {
                                    localPlayer.msg(Messages.COMMAND_POINTS_PAY_NOT_ENOUGH.getValue());
                                    return;
                                }
                                localPlayer.decrementPoints(Math.toIntExact((long) kit.getPrice()));
                                localPlayer.getRank(kit).get().increaseLevel();
                                localPlayer.msg("&aKit ranked up successfully");
                            });
                }).collect(Collectors.toList());
    }
}
