package me.aiglez.lonkskit.players.impl;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonElement;
import me.aiglez.lonkskit.KitPlugin;
import me.aiglez.lonkskit.data.MemoryLocalPlayer;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.players.LocalPlayerFactory;
import me.aiglez.lonkskit.utils.Logger;
import me.lucko.helper.gson.GsonProvider;
import me.lucko.helper.gson.JsonBuilder;
import org.bukkit.entity.Player;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class LocalPlayerFactoryImpl implements LocalPlayerFactory {

    private final Map<UUID, LocalPlayer> cache;
    private final File cacheFile;
    private final Type setType;

    public LocalPlayerFactoryImpl() {
        this.cache = new HashMap<>();
        this.cacheFile = new File(KitPlugin.getSingleton().getDataFolder(), "cache.json");
        this.setType = new TypeToken<HashSet<LocalPlayer>>(){}.getType();
    }

    @Override
    public LocalPlayer getLocalPlayer(Player player) {
        return this.cache.computeIfAbsent(player.getUniqueId(), x -> new MemoryLocalPlayer(player));
    }

    @Override
    public boolean loadLocalPlayers() {
        if(!this.cacheFile.exists()) {
            Logger.warn("Cache file not found, assuming there is no player to load...");
            return true;
        }
        try {
            final Reader reader = new FileReader(this.cacheFile);
            final HashSet<LocalPlayer> loaded = GsonProvider.prettyPrinting().fromJson(reader, setType);

            loaded.forEach(localPlayer -> cache.put(localPlayer.getUniqueId(), localPlayer));

            Logger.fine("Loaded " + cache.size() + " player(s).");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    @Override
    public boolean saveLocalPlayers() {
        if(this.cache.isEmpty()) {
            Logger.warn("No player was found to cache...");
            return true;
        }
        if(assertFile()) {
            try {
                final Writer writer = new FileWriter(cacheFile);
                final JsonElement element = JsonBuilder.array()
                        .addAll(cache.values().stream().map(LocalPlayer::serialize).collect(Collectors.toSet())).build();

                // TODO: eventually change this to {@link GsonProvider#standard} later
                GsonProvider.prettyPrinting().toJson(element, writer);
                writer.close();

                Logger.fine("Saved " + cache.size() + " player(s).");
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    private boolean assertFile() {
        if(!this.cacheFile.exists()) {
            try {
                this.cacheFile.createNewFile();
            } catch (IOException e) {
                Logger.severe("Couldn't create the cache file !");
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
}
