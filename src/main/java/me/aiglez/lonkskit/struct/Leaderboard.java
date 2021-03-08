
package me.aiglez.lonkskit.struct;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import me.aiglez.lonkskit.players.OfflineLocalPlayer;
import me.lucko.helper.Schedulers;
import me.lucko.helper.hologram.Hologram;
import me.lucko.helper.serialize.Position;
import org.bukkit.Location;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class Leaderboard {

    protected final Location location;
    protected final int refreshRate = 10;
    protected final List<OfflineLocalPlayer> sortedPlayers = Lists.newLinkedList();
    protected final List<String> sortedLines = Lists.newLinkedList();
    protected Hologram hologram;
    protected final String format;


    public static final int SIZE = 10;

    public Leaderboard(final Location location,final String format) {
        Preconditions.checkNotNull(location,"Location may not be null");
        Preconditions.checkNotNull(format,"format may not be null");
        this.location = location;
        this.format = format;
        this.reloadCache();
        this.display();
        Schedulers.sync()
                .runRepeating(this::reloadCache,refreshRate,TimeUnit.SECONDS,refreshRate,TimeUnit.SECONDS);
        Schedulers.sync()
                .runRepeating(this::reloadView,3,TimeUnit.MILLISECONDS,refreshRate,TimeUnit.SECONDS);

    }

    public void display(){
        this.hologram = Hologram.create(Position.of(this.location),this.sortedLines);
        if (!hologram.isSpawned()){
            this.hologram.spawn();
        }
        this.hologram.setClickCallback(player -> player.performCommand("kitpvp stats"));
    }

    public abstract void reloadView();

    public abstract void reloadCache();


}
