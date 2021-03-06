package me.aiglez.lonkskit.players;


import me.aiglez.lonkskit.LonksKitProvider;
import me.aiglez.lonkskit.kits.Kit;
import me.aiglez.lonkskit.kits.KitRank;
import me.lucko.helper.metadata.MetadataMap;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import java.util.Optional;

@SuppressWarnings({"unused", "RedundantSuppression"})
public interface LocalPlayer extends OfflineLocalPlayer, LocalMessager, LocalRenter, LocalRanks {

    Player toBukkit();

    void setBukkit(Player bukkit);

    Location getLocation();

    World getWorld();

    /**
     * Mirror to {@see WorldProvider#inKPWorld}
     */
    boolean isValid();

    PlayerInventory getInventory();

    Kit getNullableSelectedKit();

    boolean hasSelectedKit();

    void setSelectedKit(Kit kit);

    boolean inArena();

    void setInArena(boolean status);

    boolean isSafe();

    void setSafeStatus(boolean status);

    void updateSafeStatus();

    boolean hasAccess(Kit kit);

    void openKitSelector();

    Optional<LocalPlayer> getLastAttacker();

    void setLastAttacker(LocalPlayer localPlayer);

    MetadataMap getMetadata();

    static LocalPlayer get(Player player) {
        if(LonksKitProvider.getPlayerFactory() == null) throw new UnsupportedOperationException("The player factory isn't initialized yet!");
        return LonksKitProvider.getPlayerFactory().getLocalPlayer(player);
    }

    boolean wasInKP();

    void setInKP(boolean value);

    KitRank getKitRank();

}
