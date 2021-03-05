package me.aiglez.lonkskit.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import me.aiglez.lonkskit.messages.Replaceable;
public class Logger {

    public static void severe(String message, Object... replacements) {
        Bukkit.getConsoleSender().sendMessage(
                ChatColor.RED + "[LonksKit - SEVERE] " + ChatColor.RESET + Replaceable.handle(message, replacements)
        );
    }

    public static void fine(String message, Object... replacements) {
        Bukkit.getConsoleSender().sendMessage(
                ChatColor.GREEN + "[LonksKit - FINE] " + ChatColor.RESET + Replaceable.handle(message, replacements)
        );
    }

    public static void debug(String message, Object... replacements) {
        Bukkit.getConsoleSender().sendMessage(
                ChatColor.AQUA + "[LonksKit - DEBUG] " + ChatColor.RESET + Replaceable.handle(message, replacements)
        );
    }

    public static void warn(String message, Object... replacements) {
        Bukkit.getConsoleSender().sendMessage(
                ChatColor.YELLOW + "[LonksKit - WARN] " + ChatColor.RESET + Replaceable.handle(message, replacements)
        );
    }

    private Logger() {
        throw new UnsupportedOperationException();
    }
}
