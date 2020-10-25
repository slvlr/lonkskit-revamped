package me.aiglez.lonkskit.controllers;

public class Controllers {

    public static final PlayerController PLAYER = new PlayerController();

    public static void initControllers() {
        PLAYER.loadHotbarItems();
    }
}
