package me.aiglez.lonkskit.players;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.aiglez.lonkskit.LonksKitProvider;
import me.aiglez.lonkskit.kits.Kit;
import me.aiglez.lonkskit.players.impl.MemoryOfflineLocalPlayer;
import me.aiglez.lonkskit.utils.Logger;
import me.lucko.helper.gson.GsonSerializable;

import java.util.Optional;
import java.util.UUID;

public interface OfflineLocalPlayer extends LocalRenter, GsonSerializable {

    UUID getUniqueId();

    String getLastKnownName();

    boolean isOnline();

    LocalPlayer getOnlinePlayer();

    LocalMetrics getMetrics();

    double getPoints();

    void incrementPoints(int amount);

    boolean decrementPoints(int amount);

    static OfflineLocalPlayer deserialize(JsonElement element) {
        Preconditions.checkArgument(element.isJsonObject());
        JsonObject object = element.getAsJsonObject();

        Preconditions.checkArgument(object.has("unique-id"));
        final UUID uniqueId = UUID.fromString(object.get("unique-id").getAsString());

        Preconditions.checkArgument(object.has("points"));
        final int points = object.get("points").getAsInt();

        final OfflineLocalPlayer offlineLocalPlayer = new MemoryOfflineLocalPlayer(uniqueId, points);

        Preconditions.checkArgument(object.has("metrics"));
        final JsonObject metrics = object.getAsJsonObject("metrics");
        final int metricsDeathsCount = metrics.has("deaths") ? metrics.get("deaths").getAsInt() : 0;
        final int metricsKillsCount = metrics.has("kills") ? metrics.get("kills").getAsInt() : 0;

        offlineLocalPlayer.getMetrics().updateAll(metricsKillsCount, metricsDeathsCount);

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

            offlineLocalPlayer.addRent(LocalRent.of(offlineLocalPlayer, optionalKit.get(), uses));
        }

        return offlineLocalPlayer;
    }
}
