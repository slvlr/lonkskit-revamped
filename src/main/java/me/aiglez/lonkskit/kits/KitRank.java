package me.aiglez.lonkskit.kits;

import com.google.common.base.Preconditions;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.players.OfflineLocalPlayer;

public class KitRank {

    private final OfflineLocalPlayer offlineLocalPlayer;
    private final Kit kit;
    private int level;

    public KitRank(LocalPlayer localPlayer, Kit kit) {
        this(localPlayer,kit,1);
    }

    public KitRank(OfflineLocalPlayer localPlayer, Kit kit, int level) {
        this.offlineLocalPlayer = localPlayer;
        this.kit = kit;
        this.level = level;
    }

    public OfflineLocalPlayer getOfflineLocalPlayer() {
        return this.offlineLocalPlayer;
    }

    public LocalPlayer getLocalPlayer() {
        return this.offlineLocalPlayer.getOnlinePlayer();
    }

    public Kit getKit() {
        return kit;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        Preconditions.checkArgument(level <= 0,"level may not be negative");
        this.level = level;
    }
    public void increaseLevel() {
        this.level += 1;
    }
    public void decreaseLevel() {
        this.level -= 1;
    }
}
