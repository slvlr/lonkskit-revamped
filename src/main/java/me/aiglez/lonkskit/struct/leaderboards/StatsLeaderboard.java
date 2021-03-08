package me.aiglez.lonkskit.struct.leaderboards;

import me.aiglez.lonkskit.LonksKitProvider;
import me.aiglez.lonkskit.messages.Replaceable;
import me.aiglez.lonkskit.players.OfflineLocalPlayer;
import me.aiglez.lonkskit.struct.Leaderboard;
import me.lucko.helper.Schedulers;
import me.lucko.helper.hologram.individual.HologramLine;
import org.bukkit.Location;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public final class StatsLeaderboard extends Leaderboard {
    private final byte size = 10;
    public StatsLeaderboard(Location location, String format) {
        super(location, format);
        this.reloadCache();
        display();
        Schedulers.sync()
                .runRepeating(() -> {
                    this.reloadCache();
                    this.reloadView();
                },3, TimeUnit.MILLISECONDS,refreshRate,TimeUnit.SECONDS);

    }

    @Override
    public void reloadCache() {
        this.sortedLines.clear();
        this.sortedPlayers.clear();
        this.sortedPlayers.addAll(new ArrayList<>(LonksKitProvider.getPlayerFactory().getCachedOfflinePlayers()));
        this.sortedLines.add("§8§m----------------------------------------");
        DecimalFormat format = new DecimalFormat("#0.00");
        for (OfflineLocalPlayer localPlayer : sortedPlayers.stream().sorted(Comparator.comparingDouble(OfflineLocalPlayer::getKDR).reversed()).limit(size).collect(Collectors.toList())) {
            sortedLines.add(
                    Replaceable.handle(this.format,
                            localPlayer.getLastKnownName(),
                            localPlayer.getMetrics().getKillsCount(),
                            localPlayer.getMetrics().getDeathsCount(),
                            format.format(localPlayer.getKDR()))
            );
        }
        this.sortedLines.add("§8§m----------------------------------------");

    }

    @Override
    public void reloadView() {
        this.hologram.updateLines(this.sortedLines);
    }
}
