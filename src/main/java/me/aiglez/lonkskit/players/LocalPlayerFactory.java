package me.aiglez.lonkskit.players;

import me.aiglez.lonkskit.players.impl.LocalPlayerFactoryImpl;
import org.bukkit.entity.Player;

public interface LocalPlayerFactory {

    LocalPlayer getLocalPlayer(Player player);

    boolean loadLocalPlayers();

    boolean saveLocalPlayers();

    static LocalPlayerFactory make() {
        return new LocalPlayerFactoryImpl();
    }
}
