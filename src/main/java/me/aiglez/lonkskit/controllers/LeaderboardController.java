package me.aiglez.lonkskit.controllers;

import com.google.common.collect.Lists;
import me.aiglez.lonkskit.KitPlugin;
import me.aiglez.lonkskit.struct.Leaderboard;
import me.aiglez.lonkskit.struct.leaderboards.PointsLeaderboard;
import me.aiglez.lonkskit.struct.leaderboards.StatsLeaderboard;
import me.aiglez.lonkskit.utils.Locations;
import me.aiglez.lonkskit.utils.Logger;
import me.lucko.helper.config.ConfigurationNode;
import org.bukkit.Location;

import java.util.List;
import java.util.Optional;

public class LeaderboardController {

    private final ConfigurationNode config;
    private final List<Leaderboard<?>> leaderboards;

    public LeaderboardController() {
        this.config = KitPlugin.getSingleton().getConf().getNode("leaderboards");
        this.leaderboards = Lists.newArrayList();
    }

    public void makePointsLeaderboard() {
        final Optional<Location> location = Locations.fromString(this.config.getNode("points", "location").getString(""));
        if(location.isPresent()) {
            this.leaderboards.add(new PointsLeaderboard(location.get()));
            this.leaderboards.add(new StatsLeaderboard(location.get().add(5,0,5)));
        } else {
            Logger.severe("An error occurred while trying to load the points leaderboard, the location is invalid!");
        }
    }

}
