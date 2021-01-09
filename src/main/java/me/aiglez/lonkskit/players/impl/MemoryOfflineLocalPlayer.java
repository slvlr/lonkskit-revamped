package me.aiglez.lonkskit.players.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import me.aiglez.lonkskit.exceptions.OfflinePlayerLoadException;
import me.aiglez.lonkskit.kits.Kit;
import me.aiglez.lonkskit.players.LocalMetrics;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.players.LocalRent;
import me.aiglez.lonkskit.players.OfflineLocalPlayer;
import me.aiglez.lonkskit.utils.Logger;
import me.lucko.helper.gson.JsonBuilder;
import me.lucko.helper.utils.Players;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class MemoryOfflineLocalPlayer implements OfflineLocalPlayer {

    private final UUID uniqueId;
    private final LocalMetrics metrics;
    private final List<LocalRent> rents;

    private int points;
    private String lastKnownName;
    private LocalPlayer online;


    public MemoryOfflineLocalPlayer(UUID uniqueId, int points) {
        Preconditions.checkNotNull(uniqueId, "unique-id may not be null");
        this.uniqueId = uniqueId;
        this.metrics = LocalMetrics.newMetrics(this);
        this.rents = Lists.newArrayList();

        this.points = points;

        Logger.debug("Fetching the username of player with uuid {0}", this.uniqueId);
        final OfflinePlayer offlinePlayer = Players.getOfflineNullable(this.uniqueId);
        if(offlinePlayer == null) {
            throw new OfflinePlayerLoadException("It seems like the player with uuid [" + this.uniqueId + "] has never player on this server.");
        }

        this.lastKnownName = offlinePlayer.getName() == null ? "unknown" : offlinePlayer.getName();
        Logger.debug("Username of the player with uuid {0} is [{1}]", this.uniqueId, this.lastKnownName);
    }


    @Override
    public UUID getUniqueId() { return this.uniqueId; }

    @Override
    public String getLastKnownName() { return this.lastKnownName; }

    @Override
    public boolean isOnline() {
        return this.online != null && this.online.toBukkit() != null && this.online.toBukkit().isOnline();
    }

    @Override
    public LocalPlayer getOnlinePlayer() {
        if(this.online == null) {
            final Player player = Players.getNullable(uniqueId);
            if(player == null) {
                return null;
            }
            this.online = new MemoryLocalPlayer(this, player);
        }
        return this.online;
    }

    @Override
    public LocalMetrics getMetrics() { return this.metrics; }

    @Override
    public int getPoints() { return this.points; }

    @Override
    public void incrementPoints(int amount) {
        this.points = this.points + amount;
        if(this.points < 0) this.points = 0;
    }

    @Override
    public void setPoints(int amount) {
        if(amount < 0) this.points = 0;
        this.points = amount;
    }

    @Override
    public boolean decrementPoints(int amount) {
        if(amount < 0 || amount > this.points) return false;
        this.points = this.points - amount;
        return true;
    }

    @Override
    public List<LocalRent> getRents() { return Collections.unmodifiableList(this.rents); }

    @Override
    public Optional<LocalRent> getRent(Kit kit) {
        Preconditions.checkNotNull(kit, "kit may not be null");
        return this.rents.stream().filter(localRent -> localRent.getRented().equals(kit)).findFirst();
    }

    @Override
    public boolean hasRented(Kit kit) { return this.rents.stream().map(LocalRent::getRented).anyMatch(k -> k.equals(kit)); }

    @Override
    public void addRent(LocalRent rent) {
        Preconditions.checkNotNull(rent, "rent may not be null");
        Preconditions.checkArgument(rent.isValid(), "rent must be valid");
        this.rents.add(rent);
    }

    @Override
    public void removeRent(LocalRent rent) {
        Preconditions.checkNotNull(rent, "rent may not be null");
        this.rents.remove(rent);
    }

    @Nonnull
    @Override
    public JsonElement serialize() {
        return JsonBuilder.object()
                .add("unique-id", this.uniqueId.toString())
                .add("points", this.points)

                .add("metrics", JsonBuilder.object()
                        .add("deaths", this.metrics.getDeathsCount())
                        .add("kills", this.metrics.getKillsCount())
                        .build()
                )
                .add("rents", JsonBuilder.array()
                        .addAll(
                                this.rents.stream().map(rent -> JsonBuilder.object()
                                        .add("uses", rent.getUses())
                                        .add("kit", rent.getRented().getBackendName())
                                        .build()
                                ).collect(Collectors.toList())
                        ).build()
                )
                .build();
    }
}
