package me.aiglez.lonkskit.struct.leaderboards;

import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import com.google.common.collect.Lists;
import me.aiglez.lonkskit.LonksKitProvider;
import me.aiglez.lonkskit.messages.Replaceable;
import me.aiglez.lonkskit.players.OfflineLocalPlayer;
import me.aiglez.lonkskit.struct.Leaderboard;
import org.bukkit.Location;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class PointsLeaderboard extends Leaderboard<OfflineLocalPlayer> {

    public PointsLeaderboard(Location location) {
        super("points", location, 2, TimeUnit.SECONDS, "&a{0} &7- &e{1} &7point(s)");
        this.cache = Lists.newLinkedList();
    }
    @Override
    public void reloadCache() {
        this.cache.clear();
        this.cache.addAll(
                LonksKitProvider.getPlayerFactory().getCachedOfflinePlayers()
                .stream()
                .filter(offlineLocalPlayer -> offlineLocalPlayer.getPoints() >= 1)
                .sorted(Comparator.comparingInt(OfflineLocalPlayer::getPoints).reversed()).limit(10)
                .collect(Collectors.toSet())
        );
    }

    @Override
    public void reloadView() {
        if(!this.cache.isEmpty()) {
            this.hologram.clearLines();
        }
        for (int i = 0; i < SIZE; i++) {
            final TextLine line = this.lines.get(i);
            try {
                final OfflineLocalPlayer offlineLocalPlayer = this.cache.get(i);
                line.setText(Replaceable.handle(this.format, offlineLocalPlayer.getLastKnownName(), offlineLocalPlayer.getPoints()));
            } catch (IndexOutOfBoundsException e) {
                line.setText("");
            }
            this.hologram.appendTextLine(line.getText());
            return;
        }
    }

}
