package me.aiglez.lonkskit.players.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;
import com.google.gson.JsonElement;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.players.LocalPlayerFactory;
import me.aiglez.lonkskit.players.OfflineLocalPlayer;
import me.aiglez.lonkskit.utils.Logger;
import me.lucko.helper.gson.GsonProvider;
import me.lucko.helper.gson.JsonBuilder;
import org.bukkit.entity.Player;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class LocalPlayerFactoryImpl implements LocalPlayerFactory {

    private final Set<OfflineLocalPlayer> cache;
    private final File cacheFile;
    private final Type setType;

    public LocalPlayerFactoryImpl() {
        this.cache = Sets.newHashSet();
        this.cacheFile = new File(Helper.hostPlugin().getDataFolder(), "cache.json");
        this.setType = new TypeToken<HashSet<OfflineLocalPlayer>>(){}.getType();
    }


    @Override
    public OfflineLocalPlayer getOfflineLocalPlayer(UUID uniqueId) {
        OfflineLocalPlayer found = null;
        for (OfflineLocalPlayer offlineLocalPlayer : this.cache) {
            if(offlineLocalPlayer.getUniqueId().equals(uniqueId)) {
                found = offlineLocalPlayer;
            }
        }

        if(found == null) {
            found = new MemoryOfflineLocalPlayer(uniqueId, 0);
            Logger.debug("The player with unique id {0} was not found in cache creating a new instance", uniqueId);
            this.cache.add(found);
        }

        return found;
    }

    @Override
    public Optional<OfflineLocalPlayer> getOfflineLocalPlayer(String name) {
        Preconditions.checkNotNull(name, "name may not be null");
        return this.cache.stream().filter(offlineLocalPlayer -> offlineLocalPlayer.getLastKnownName().equalsIgnoreCase(name)).findFirst();
    }

    @Override
    public LocalPlayer getLocalPlayer(Player player) {
        Preconditions.checkNotNull(player, "player may not be null");
        return getOfflineLocalPlayer(player.getUniqueId()).getOnlinePlayer();
    }

    @Override
    public Set<OfflineLocalPlayer> getCachedOfflinePlayers() {
        return Collections.unmodifiableSet(this.cache);
    }

    @Override
    public boolean loadOfflineLocalPlayers() {
        if(!this.cacheFile.exists()) {
            Logger.warn("Cache file not found, assuming there is no player to load...");
            return true;
        }
        try {
            final Reader reader = new FileReader(this.cacheFile);
            final HashSet<OfflineLocalPlayer> loaded = GsonProvider.standard().fromJson(reader,setType);

            this.cache.addAll(loaded);

            Logger.fine("Loaded " + this.cache.size() + " player(s).");
        } catch (IOException e) {
            Logger.warn("Error with Cache File");
            e.getCause().printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean saveOfflineLocalPlayers() {
        if(this.cache.isEmpty()) {
            Logger.warn("No player was found to cache...");
            return true;
        }
        if(assertFileExists()) {
            try {
                final Writer writer = new FileWriter(cacheFile);
                final JsonElement element = JsonBuilder.array().addAll(
                        this.cache.stream().map(OfflineLocalPlayer::serialize).collect(Collectors.toSet())
                ).build();

                // TODO: eventually change this to {@link GsonProvider#standard} later
                GsonProvider.standard().toJson(element, writer);
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

    private boolean assertFileExists() {
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

    private static Class<?> getCallerClass() {
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String clazzName = stackTrace[3].getClassName();
        try {
            return Class.forName(clazzName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
