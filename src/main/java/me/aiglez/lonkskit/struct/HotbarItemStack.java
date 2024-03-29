package me.aiglez.lonkskit.struct;

import com.google.common.base.Preconditions;
import me.aiglez.lonkskit.guis.KitSelectorGUI;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.lucko.helper.Helper;
import org.bukkit.inventory.ItemStack;
import java.util.List;

public class HotbarItemStack {

    private final ItemStack item;
    private final List<String> playerCommands, consoleCommands;
    private final int order;

    public HotbarItemStack(final ItemStack item, List<String> playerCommands, List<String> consoleCommands, String name, int order) {
        this.order = order;
        Preconditions.checkNotNull(item, "item may not be null");
        Preconditions.checkNotNull(playerCommands, "player commands may not be null");
        Preconditions.checkNotNull(consoleCommands, "console commands may not be null");
        this.item = item;
        this.playerCommands = playerCommands;
        this.consoleCommands = consoleCommands;
    }

    public void runCommands(LocalPlayer localPlayer) {
        playerCommands.forEach(command -> {
            if(command.equalsIgnoreCase("/kits")) {
                new KitSelectorGUI(localPlayer).open();
            } else {
                localPlayer.toBukkit().performCommand(command.replace("{player}", localPlayer.getLastKnownName()));
            }
        });
        consoleCommands.forEach(command -> Helper.executeCommand(command.replace("{player}", localPlayer.getLastKnownName())));
    }

    public ItemStack getItemStack() { return item; }

    public int getOrder() {
        return order;
    }
}

