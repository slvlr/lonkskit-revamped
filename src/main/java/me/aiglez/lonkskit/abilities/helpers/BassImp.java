package me.aiglez.lonkskit.abilities.helpers;

import me.aiglez.lonkskit.players.LocalPlayer;
import me.lucko.helper.Schedulers;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class BassImp {
    private static final List<LocalPlayer> playerList = new ArrayList<>();
    private final LocalPlayer localPlayer;
    private double progress;
    private final String title;
    private BossBar bossBar;
    public BassImp(LocalPlayer localPlayer,String title) {
        this.localPlayer = localPlayer;
        this.progress = 0;
        this.title = title;
    }
    public void runnable(Consumer<LocalPlayer> localPlayerConsumer){

        if (!playerList.contains(localPlayer)) {
            playerList.add(localPlayer);
            this.bossBar = Bukkit.createBossBar(this.title, BarColor.RED, BarStyle.SOLID, BarFlag.CREATE_FOG);
            this.bossBar.setProgress(0);
            this.bossBar.addPlayer(localPlayer.toBukkit());
            this.bossBar.setVisible(true);
            Schedulers.sync().runRepeating(run -> {
                if (localPlayer.toBukkit().isSneaking()){
                    if (bossBar.getProgress() < 1) {
                        bossBar.setProgress(progress);
                        localPlayer.msg("&a Charging for " + StringUtils.split(Double.toString(progress),".")[0].toCharArray()[1] +"&a0%");
                    }else {
                        bossBar.setVisible(false);
                        bossBar.removePlayer(localPlayer.toBukkit());
                        bossBar.removeAll();
                        localPlayerConsumer.accept(localPlayer);
                        run.stop();
                        run.close();
                        playerList.remove(localPlayer);
                    }
                }else {
                    bossBar.setVisible(false);
                    bossBar.removePlayer(localPlayer.toBukkit());
                    bossBar.removeAll();
                    run.stop();
                    run.close();
                    playerList.remove(localPlayer);

                }
                progress += 0.1;
            },3L,20L);
        }

    }
}
