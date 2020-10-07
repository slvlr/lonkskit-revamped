package me.aiglez.lonkskit.players;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.aiglez.lonkskit.LonksKitProvider;
<<<<<<< HEAD
import me.aiglez.lonkskit.data.MemoryLocalPlayer;
import me.aiglez.lonkskit.kits.Kit;
=======
import me.aiglez.lonkskit.WorldProvider;
import me.aiglez.lonkskit.data.MemoryLocalPlayer;
import me.aiglez.lonkskit.kits.Kit;
import me.aiglez.lonkskit.kits.KitSelectorGUI;
import me.aiglez.lonkskit.players.messages.Replaceable;
>>>>>>> 652ee15... Track all files
import me.aiglez.lonkskit.utils.Logger;
import me.lucko.helper.gson.GsonSerializable;
import me.lucko.helper.metadata.Metadata;
import me.lucko.helper.metadata.MetadataMap;
<<<<<<< HEAD
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

=======
import me.lucko.helper.text3.Text;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;
>>>>>>> 652ee15... Track all files
import java.util.Optional;
import java.util.UUID;

@SuppressWarnings("unused")
<<<<<<< HEAD
public interface LocalPlayer extends GsonSerializable, LocalMessager, LocalRenter {
=======
public interface LocalPlayer extends GsonSerializable {
>>>>>>> 652ee15... Track all files

    UUID getUniqueId();

    String getLastKnownName();

    Player toBukkit();

<<<<<<< HEAD
    void setBukkit(Player bukkit);

    Location getLocation();

    World getWorld();

    PlayerInventory getInventory();

    LocalMetrics getMetrics();

    Kit getNullableSelectedKit();

    boolean hasSelectedKit();
=======
    default Location getLocation() {
        if(toBukkit() != null) return toBukkit().getLocation();
        return new Location(WorldProvider.KP_WORLD, 0, 0, 0);
    }

    default World getWorld() {
        if(toBukkit() != null) return WorldProvider.MAIN_WORLD;
        return toBukkit().getWorld();
    }

    void setBukkit(Player bukkit);

    LocalMetrics getMetrics();

    void updateMetrics(int killsCount, int deathsCount);

    List<LocalRent> getRents();

    default Optional<LocalRent> getRent(Kit kit) {
        if(kit == null) return Optional.empty();
        for (final LocalRent rent : getRents()) {
            if(rent.getRented().equals(kit)) return Optional.of(rent);
        }
        return Optional.empty();
    }

    boolean hasRented(Kit kit);

    void addRent(LocalRent rent);

    void removeRent(LocalRent rent);

    Kit getNullableSelectedKit();

    default boolean hasSelectedKit() {
        return getNullableSelectedKit() != null;
    }
>>>>>>> 652ee15... Track all files

    boolean setSelectedKit(Kit kit);

    double getPoints();

    void incrementPoints(int by);

    boolean decrementPoints(int by);

    boolean atArena();

    void setAtArena(boolean bool);

    boolean isSafe();

    void setSafeStatus(boolean status);

<<<<<<< HEAD
=======
    void updateSafeStatus();

>>>>>>> 652ee15... Track all files
    default boolean hasAccess(Kit kit) {
        if(kit == null || !kit.enabled() || toBukkit() == null) return false;
        return toBukkit().hasPermission("lonkskit." + kit.getBackendName());
    }

<<<<<<< HEAD
    void openKitSelector();
=======
    default void openKitSelector() {
        if(toBukkit() != null) {
            new KitSelectorGUI(this).open();
        }
    }
>>>>>>> 652ee15... Track all files

    default MetadataMap metadata() {
        return Metadata.provideForPlayer(getUniqueId());
    }

<<<<<<< HEAD
=======
    default void msg(String message) {
        Preconditions.checkNotNull(message, "message may not be null");
        if(toBukkit() != null) toBukkit().sendMessage(Text.colorize(message));
    }

    default void msg(Iterable<String> messages) {
        if(toBukkit() != null) {
            messages.forEach(this::msg);
        }
    }

    default void msg(String message, Object... replacements) {
        msg(Replaceable.handle(message, replacements));
    }

>>>>>>> 652ee15... Track all files

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
<<<<<<< HEAD
        localPlayer.getMetrics().updateAll(metricsKillsCount, metricsDeathsCount);
=======
        localPlayer.updateMetrics(metricsKillsCount, metricsDeathsCount);
>>>>>>> 652ee15... Track all files

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
