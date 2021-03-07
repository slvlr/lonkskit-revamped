package me.aiglez.lonkskit.struct.leaderboards;

import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import com.google.common.collect.Lists;
import me.aiglez.lonkskit.LonksKitProvider;
import me.aiglez.lonkskit.messages.Replaceable;
import me.aiglez.lonkskit.players.LocalMetrics;
import me.aiglez.lonkskit.players.OfflineLocalPlayer;
import me.aiglez.lonkskit.struct.Leaderboard;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class StatsLeaderboard extends Leaderboard<OfflineLocalPlayer> {

    public StatsLeaderboard(Location location) {
        super("Statistics", location, 2, TimeUnit.SECONDS, "&a{0} &7 - &a{1} Kills &7 - &c{2} Deaths - &e{3} K/D Ratio");
        this.cache = Lists.newLinkedList();
    }

    @Override
    public void reloadCache() {
        this.cache.clear();
        this.cache = new ArrayList<>(LonksKitProvider.getPlayerFactory().getCachedOfflinePlayers());
        this.cache.stream().map(OfflineLocalPlayer::getMetrics)
                .sorted(Comparator.comparing(LocalMetrics::getKDR).reversed())
                .map(LocalMetrics::getHolder)
                .collect(Collectors.toSet());
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
                line.setText(Replaceable.handle(this.format, offlineLocalPlayer.getLastKnownName(), offlineLocalPlayer.getMetrics().getKillsCount(),offlineLocalPlayer.getMetrics().getDeathsCount(),offlineLocalPlayer.getMetrics().getKDR()));
            } catch (IndexOutOfBoundsException e) {
                line.setText("");
            }
            this.hologram.appendTextLine(line.getText());
            return;
        }
    }
    }
