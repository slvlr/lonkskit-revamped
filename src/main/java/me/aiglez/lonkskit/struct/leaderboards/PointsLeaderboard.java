package me.aiglez.lonkskit.struct.leaderboards;

import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import me.aiglez.lonkskit.LonksKitProvider;
import me.aiglez.lonkskit.players.OfflineLocalPlayer;
import me.aiglez.lonkskit.players.messages.Replaceable;
import me.aiglez.lonkskit.struct.Leaderboard;
import me.aiglez.lonkskit.utils.Logger;
import org.bukkit.Location;

import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class PointsLeaderboard extends Leaderboard<OfflineLocalPlayer> {

    public PointsLeaderboard(Location location) {
        super("points", location, 50, TimeUnit.SECONDS, "&a{0} &7- &e{1} &7point(s)");
        this.cache = Lists.newLinkedList();
    }

    @Override
    public void setComparator() {
        this.comparator = (o1, o2) -> Ints.compare(o1.getPoints(), o2.getPoints());
    }

    @Override
    public void reloadCache() {
        final long start = System.currentTimeMillis();

        this.cache.clear();
        this.cache.addAll(
                LonksKitProvider.getPlayerFactory().getCachedOfflinePlayers()
                .stream()
                .filter(offlineLocalPlayer -> offlineLocalPlayer.getPoints() >= 1)
                .sorted(this.comparator).limit(10)
                .collect(Collectors.toSet())
        );

        Logger.debug("Reloaded cache of [{0}] took: {1} ms, cached {2} instances.", this.name, (System.currentTimeMillis() - start), this.cache.size());
    }

    @Override
    public void reloadView() {
        final long start = System.currentTimeMillis();

        if(this.cache.isEmpty()) {
            this.hologram.clearLines();
            return;
        }

        int changed = 0, notFound = 0;
        for (int i = 0; i < SIZE; i++) {
            final TextLine line = this.lines.get(i);
            try {
                final OfflineLocalPlayer offlineLocalPlayer = this.cache.get(i);

                line.setText(Replaceable.handle(this.format, offlineLocalPlayer.getLastKnownName(), offlineLocalPlayer.getPoints()));
                changed++;
            } catch (IndexOutOfBoundsException e) {
                line.setText("");
                notFound++;
            }
        }

        Logger.debug("Reloaded the view of [{0}] took: {1} ms, changed: {2} | not found: {3}", this.name, (System.currentTimeMillis() - start), changed, notFound);

    }

}
