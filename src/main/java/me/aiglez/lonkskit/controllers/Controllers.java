package me.aiglez.lonkskit.controllers;

public class Controllers {

    public static final PlayerController PLAYER = new PlayerController();

    public static final LeaderboardController LEADERBOARD = new LeaderboardController();

    public static void initControllers() {
        PLAYER.loadHotbarItems();
        LEADERBOARD.makePointsLeaderboard();
    }
}
