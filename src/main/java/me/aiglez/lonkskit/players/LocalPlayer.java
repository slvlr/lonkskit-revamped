package me.aiglez.lonkskit.players;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.aiglez.lonkskit.LonksKitProvider;
import me.aiglez.lonkskit.data.MemoryLocalPlayer;
import me.aiglez.lonkskit.kits.Kit;
import me.aiglez.lonkskit.utils.Logger;
import me.lucko.helper.gson.GsonSerializable;
import me.lucko.helper.metadata.Metadata;
import me.lucko.helper.metadata.MetadataMap;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import java.util.Optional;
import java.util.UUID;

@SuppressWarnings("unused")
public interface LocalPlayer extends GsonSerializable, LocalMessager, LocalRenter {

    UUID getUniqueId();

    String getLastKnownName();

    Player toBukkit();

    void setBukkit(Player bukkit);

    Location getLocation();

    World getWorld();

    PlayerInventory getInventory();

    LocalMetrics getMetrics();

    Kit getNullableSelectedKit();

    boolean hasSelectedKit();

    boolean setSelectedKit(Kit kit);

    double getPoints();

    void incrementPoints(int by);

    boolean decrementPoints(int by);

    boolean atArena();

    void setAtArena(boolean bool);

    boolean isSafe();

    void setSafeStatus(boolean status);

    default boolean hasAccess(Kit kit) {
        if(kit == null || !kit.enabled() || toBukkit() == null) return false;
        return toBukkit().hasPermission("lonkskit." + kit.getBackendName());
    }

    void openKitSelector();

    default MetadataMap metadata() {
        return Metadata.provideForPlayer(getUniqueId());
    }


    static LocalPlayer get(Player player) {
        if(LonksKitProvider.getPlayerFactory() == null) throw new UnsupportedOperationException("The player factory isn't initialized yet!");
        return LonksKitProvider.getPlayerFactory().getLocalPlayer(player);
    }

    static LocalPlayer deserialize(JsonElement element) {
        Preconditions.checkArgument(element.isJsonObject());
        JsonObject object = element.getAsJsonObject();

        Preconditions.checkArgument(object.has("unique-id"));
        final UUID uniqueId = UUID.fromString(object.get("unique-id").getAsString());

        Preconditions.checkArgument(object.has("points"));
        final int points = object.get("points").getAsInt();

        final LocalPlayer localPlayer = new MemoryLocalPlayer(uniqueId, points);

        Preconditions.checkArgument(object.has("metrics"));
        final JsonObject metrics = object.getAsJsonObject("metrics");
        final int metricsDeathsCount = metrics.has("deaths") ? metrics.get("deaths").getAsInt() : 0;
        final int metricsKillsCount = metrics.has("kills") ? metrics.get("kills").getAsInt() : 0;
        localPlayer.getMetrics().updateAll(metricsKillsCount, metricsDeathsCount);

        Preconditions.checkArgument(object.has("rents"));
        final JsonArray rents = object.getAsJsonArray("rents");

        for (JsonElement rent : rents) {
            Preconditions.checkArgument(rent.isJsonObject());
            JsonObject rentObject = rent.getAsJsonObject();

            Preconditions.checkArgument(rentObject.has("uses"));
            final int uses = rentObject.get("uses").getAsInt();

            Preconditions.checkArgument(rentObject.has("kit"));
            final Optional<Kit> optionalKit = LonksKitProvider.getKitFactory().getKit(rentObject.get("kit").getAsString());
            if (!optionalKit.isPresent()) {
                Logger.warn("No kit with backend-name [" + rentObject.get("kit").getAsString() + "] was found, (Rent deserialization)");
                continue;
            }

            localPlayer.addRent(LocalRent.of(localPlayer, optionalKit.get(), uses));
        }

        return localPlayer;
    }


    @Override
    JsonElement serialize();
}
