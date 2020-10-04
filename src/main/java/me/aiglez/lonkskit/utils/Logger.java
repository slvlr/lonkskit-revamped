package me.aiglez.lonkskit.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Logger {

    public static void severe(String message) {
        Bukkit.getConsoleSender().sendMessage( ChatColor.RED + "[LonksKit - SEVERE] " + ChatColor.RESET + message);
    }

    public static void fine(String message) {
        Bukkit.getConsoleSender().sendMessage( ChatColor.GREEN + "[LonksKit - FINE] " + ChatColor.RESET + message);
    }

    public static void debug(String message) {
        Bukkit.getConsoleSender().sendMessage( ChatColor.AQUA + "[LonksKit - DEBUG] " + ChatColor.RESET + message);
    }

    public static void warn(String message) {
        Bukkit.getConsoleSender().sendMessage( ChatColor.YELLOW + "[LonksKit - WARN] " + ChatColor.RESET + message);
    }

    private Logger() {
        throw new UnsupportedOperationException();
    }
}
