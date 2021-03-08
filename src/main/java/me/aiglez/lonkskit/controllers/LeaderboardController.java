package me.aiglez.lonkskit.controllers;

import com.google.common.collect.Lists;
import me.aiglez.lonkskit.KitPlugin;
import me.aiglez.lonkskit.struct.Leaderboard;
import me.aiglez.lonkskit.struct.leaderboards.PointsLeaderboard;
import me.aiglez.lonkskit.struct.leaderboards.StatsLeaderboard;
import me.aiglez.lonkskit.utils.Locations;
import me.aiglez.lonkskit.utils.Logger;
import me.lucko.helper.Services;
import me.lucko.helper.config.ConfigurationNode;
import org.bukkit.Location;
import java.util.List;
import java.util.Optional;

public class LeaderboardController {

    private final ConfigurationNode config;
    private final List<Leaderboard> leaderboards;

    public LeaderboardController() {
        this.config = Services.load(KitPlugin.class).getConf().getNode("leaderboards");
        this.leaderboards = Lists.newArrayList();
    }

    public void makePointsLeaderboard() {
        final Optional<Location> location = Locations.fromString(this.config.getNode("points", "location").getString(""));
        final Optional<Location> statsLocation = Locations.fromString(this.config.getNode("stats","location").getString(""));
        if(location.isPresent()) {
            this.leaderboards.add(new StatsLeaderboard(statsLocation.get(),"&a{0} &7- &a{1} &7- &a{2} &7- &e{3}K/D Ratio"));
            this.leaderboards.add(new PointsLeaderboard(location.get(),"&a{0} &7- &e{1} &7 point(s)"));
        } else {
            Logger.severe("An error occurred while trying to load the points leaderboard, the location is invalid!");
        }
    }

}
