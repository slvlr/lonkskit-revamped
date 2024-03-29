package me.aiglez.lonkskit.guis;

import me.aiglez.lonkskit.LonksKitProvider;
import me.aiglez.lonkskit.commands.MainCommand;
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

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class KitSelectorGUI extends PaginatedGui {

    private static final PaginatedGuiBuilder SETTINGS;
    static  {
        final MenuScheme scheme = new MenuScheme(StandardSchemeMappings.EMPTY);
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

        SETTINGS = PaginatedGuiBuilder.create().title("Kit Selector")
                .itemSlots(itemSlots)
                .scheme(scheme)
                .nextPageSlot(nextPageSlot).previousPageSlot(previousPageSlot);
    }

    public KitSelectorGUI(LocalPlayer localPlayer) {
        super(getContent(localPlayer), localPlayer.toBukkit(), SETTINGS);
    }

    private static Function<PaginatedGui, List<Item>> getContent(LocalPlayer localPlayer) {
        final int[] i = {0};
        return gui ->
                LonksKitProvider.getKitFactory().getSortedKitsBySlots().stream().filter(kit -> !kit.isCustom())
                .map(kit -> {
                    final KitSelectorHolder holder = kit.getSelectorHolder();
                    if(!holder.display()) {
                        Logger.severe("A weird error occurred (kit selector holder not found for kit " + kit.getBackendName() + ")");
                        return ItemStackBuilder.of(Material.ITEM_FRAME).build(() -> localPlayer.msg("Hi"));
                    }

                    // has rented the kit
                    if(localPlayer.hasRented(kit)) {
                        final LocalRent localRent = localPlayer.getRent(kit).orElseThrow(() -> new IllegalStateException("A weird error has occurred, LocalPlayer#hasRented returns true, but LocalPlayer#getRent returns nothing!"));
                        return ItemStackBuilder
                                .of(holder.buildItem(kit, State.RENTED, localRent.getLeftUses()))
                                .build(() -> {
                                    if (!MainCommand.check(localPlayer)){
                                        if (!localPlayer.hasSelectedKit()) {
                                            if (localPlayer.isValid()) {
                                                if (localRent.getLeftUses() > 0) {
                                                    localPlayer.msg(Messages.SELECTOR_SELECTED, kit.getDisplayName());
                                                    gui.close();
                                                    localRent.incrementUses();
                                                    localPlayer.setSelectedKit(kit);
                                                    if (localRent.getLeftUses() <= 0) {
                                                        localRent.getOfflineLocalPlayer().removeRent(localRent);
                                                    }

                                                }else  localPlayer.msg("&eUses left = " + localRent.getLeftUses());
                                            }else  localPlayer.msg("&3enter the kitpvp world to select a kit");
                                        }else localPlayer.msg("&3clear your kit then choose another one");
                                    }else localPlayer.msg("&4You can't choose a kit cause you have a 'Throwable' item");
                                });
                    } else {
                        // has permanent access
                        if(localPlayer.hasAccess(kit)) {
                            return ItemStackBuilder
                                    .of(holder.buildItem(kit, State.PERMANENT_ACCESS, 0))
                                    .build(() -> {
                                        if (!MainCommand.check(localPlayer)){
                                            if (!localPlayer.hasSelectedKit()) {
                                                if (localPlayer.isValid()) {
                                                    localPlayer.setSelectedKit(kit);
                                                    localPlayer.msg(Messages.SELECTOR_SELECTED, kit.getDisplayName());
                                                    gui.close();
                                                }else localPlayer.msg("&3you should join the kitpvp world to select a kit");
                                            }else localPlayer.msg("&3clear your kit then choose another one");
                                        }else localPlayer.msg("&4You can't choose a kit cause you have a 'Throwable' item");
                                    });
                            // no access
                        } else {
                            return ItemStackBuilder
                                    .of(holder.buildItem(kit, State.NO_ACCESS, 0))
                                    .build(() -> {
                                        if(kit.isRentable()) {
                                            final boolean transaction = localPlayer.decrementPoints(kit.getRentCost());
                                            if(!transaction) {
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
                })
                .collect(Collectors.toList());

    }

    public enum State {

        PERMANENT_ACCESS,
        NO_ACCESS,
        RENTED

    }
}
