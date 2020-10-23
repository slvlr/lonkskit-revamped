package me.aiglez.lonkskit.struct;

import com.google.common.base.Preconditions;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class LoginItemStack {

    private final ItemStack item;
    private final List<String> playerCommands, consoleCommands;

    public LoginItemStack(final ItemStack item, List<String> playerCommands, List<String> consoleCommands) {
        Preconditions.checkNotNull(item);
        Preconditions.checkNotNull(playerCommands);
        Preconditions.checkNotNull(consoleCommands);
        this.item = item;
        this.playerCommands = playerCommands;
        this.consoleCommands = consoleCommands;
    }

    public ItemStack getItemStack() { return item; }
}
