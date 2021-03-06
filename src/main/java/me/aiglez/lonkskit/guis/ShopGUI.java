package me.aiglez.lonkskit.guis;

import me.aiglez.lonkskit.LonksKitProvider;
import me.aiglez.lonkskit.commands.MainCommand;
import me.aiglez.lonkskit.kits.KitRank;
import me.aiglez.lonkskit.kits.KitSelectorHolder;
import me.aiglez.lonkskit.messages.Messages;
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
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ShopGUI extends PaginatedGui {
    private static final PaginatedGuiBuilder SETTINGS;
    private final LocalPlayer localPlayer;
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
        this.localPlayer = localPlayer;
    }
    private static Function<PaginatedGui,List<Item>> getContent(LocalPlayer localPlayer) {
        return gui -> LonksKitProvider.getKitFactory().getEnabledKits().stream()
                .map(kit -> {
//                    if (!holder.display()) {
//                        Logger.severe("A weird error occurred (kit selector holder not found for kit " + kit.getBackendName() + ")");
//                        return ItemStackBuilder.of(Material.ITEM_FRAME).build(() -> localPlayer.msg("Hi"));
//                    }
                    if (localPlayer.hasRented(kit)) {
                        final LocalRent localRent = localPlayer.getRent(kit).orElseThrow(() -> new IllegalStateException("A weird error has occurred, LocalPlayer#hasRented returns true, but LocalPlayer#getRent returns nothing!"));
                        return ItemStackBuilder
                                .of(KitSelectorHolder.buildItemR(kit, KitSelectorGUI.State.RENTED, localRent.getLeftUses(), localPlayer))
                                .build(() -> {
                                    if (!MainCommand.check(localPlayer)) {
                                        if (!localPlayer.hasSelectedKit()) {
                                            if (localPlayer.isValid()) {
                                                localPlayer.getRank(kit).get().increaseLevel();
                                            } else localPlayer.msg("&3enter the kitpvp world to select a kit");
                                        } else localPlayer.msg("&3clear your kit then choose another one");
                                    } else
                                        localPlayer.msg("&4You can't choose a kit cause you have a 'Throwable' item");
                                });
                    } else {
                        // has permanent access
                        if (localPlayer.hasAccess(kit)) {
                            return ItemStackBuilder
                                    .of(KitSelectorHolder.buildItemR(kit, KitSelectorGUI.State.PERMANENT_ACCESS, 0, localPlayer))
                                    .build(() -> {
                                        if (!MainCommand.check(localPlayer)) {
                                            if (!localPlayer.hasSelectedKit()) {
                                                if (localPlayer.isValid()) {
                                                        localPlayer.setSelectedKit(kit);
                                                        localPlayer.msg(Messages.SELECTOR_SELECTED, kit.getDisplayName());
                                                        gui.close();
                                                } else
                                                    localPlayer.msg("&3you should join the kitpvp world to select a kit");
                                            } else localPlayer.msg("&3clear your kit then choose another one");
                                        } else
                                            localPlayer.msg("&4You can't choose a kit cause you have a 'Throwable' item");
                                    });
                            // no access
                        } else {
                            return ItemStackBuilder
                                    .of(KitSelectorHolder.buildItemR(kit, KitSelectorGUI.State.NO_ACCESS, 0, localPlayer))
                                    .build(() -> {
                                        if (kit.isRentable()) {
                                            final boolean transaction = localPlayer.decrementPoints(kit.getRentCost());
                                            if (!transaction) {
                                                localPlayer.msg(Messages.SELECTOR_RENT_FAILED);
                                                return;
                                            }
                                            final LocalRent newRent = LocalRent.of(localPlayer, kit);
                                            localPlayer.addRent(newRent);
                                            localPlayer.msg(Messages.SELECTOR_RENT_RENTED, kit.getDisplayName(), kit.getRentCost(), kit.getUsesPerRent());
                                            gui.close();
                                        } else {
                                            localPlayer.msg(Messages.SELECTOR_NO_ACCESS);
                                        }
                                    });
                        }


                    }
                }).collect(Collectors.toList());
    }
}
