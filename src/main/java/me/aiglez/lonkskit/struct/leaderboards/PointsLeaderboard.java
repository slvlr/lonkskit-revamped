package me.aiglez.lonkskit.struct.leaderboards;


import me.aiglez.lonkskit.LonksKitProvider;
import me.aiglez.lonkskit.messages.Replaceable;
import me.aiglez.lonkskit.players.OfflineLocalPlayer;
import me.aiglez.lonkskit.struct.Leaderboard;
import me.lucko.helper.Schedulers;
import org.bukkit.Location;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public final class PointsLeaderboard extends Leaderboard {
    public PointsLeaderboard(Location location, String format) {
        super(location, format);
        this.reloadCache();
        display();
        Schedulers.sync()
                .runRepeating(() -> {
                    this.reloadCache();
                    this.reloadView();
                },3,TimeUnit.MILLISECONDS,refreshRate,TimeUnit.SECONDS);

    }

    @Override
    public void reloadCache() {
        this.sortedLines.clear();
        this.sortedPlayers.clear();
        this.sortedPlayers.addAll(new ArrayList<>(LonksKitProvider.getPlayerFactory().getCachedOfflinePlayers()));
        this.sortedLines.add("§8§m----------------------------------------");
        for (OfflineLocalPlayer localPlayer : sortedPlayers.stream().sorted(Comparator.comparingDouble(OfflineLocalPlayer::getPoints).reversed()).limit(SIZE).collect(Collectors.toList())) {
            sortedLines.add(
                    Replaceable.handle(this.format,
                            localPlayer.getLastKnownName(),
                            localPlayer.getPoints()
                )
            );
        }
        this.sortedLines.add("§8§m----------------------------------------");
    }

    @Override
    public void reloadView() {
        this.hologram.updateLines(sortedLines);
    }
}
