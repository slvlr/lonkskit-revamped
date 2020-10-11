package me.aiglez.lonkskit.data;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import me.aiglez.lonkskit.events.KitSelectEvent;
import me.aiglez.lonkskit.kits.Kit;
import me.aiglez.lonkskit.kits.KitSelectorGUI;
import me.aiglez.lonkskit.players.LocalMetrics;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.players.LocalRent;
import me.aiglez.lonkskit.players.messages.Replaceable;
import me.aiglez.lonkskit.utils.Logger;
import me.lucko.helper.Events;
import me.lucko.helper.gson.JsonBuilder;
import me.lucko.helper.profiles.MojangApi;
import me.lucko.helper.text3.Text;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class MemoryLocalPlayer implements LocalPlayer {

    // to serialize
    private final UUID uniqueId;
    private final List<LocalRent> rents;
    private final AtomicInteger points;
    private final LocalMetrics metrics;

    private String lastKnownName;
    private Player bukkit;
    private Kit selectedKit;
    private boolean safe, atArena;

    public MemoryLocalPlayer(Player player) {
        this.uniqueId = player.getUniqueId();
        this.rents = Lists.newArrayList();
        this.points = new AtomicInteger(0);
        this.lastKnownName = player.getName();
        this.bukkit = player;
        this.safe = true;
        this.metrics = LocalMetrics.newMetrics(this);
    }

    public MemoryLocalPlayer(UUID uniqueId, int points) {
        this.uniqueId = uniqueId;
        this.rents = Lists.newArrayList();
        this.points = new AtomicInteger(points);
        this.safe = true;
        this.metrics = LocalMetrics.newMetrics(this);
    }

    @Override
    public UUID getUniqueId() { return this.uniqueId; }

    @Override
    public String getLastKnownName() {
        if(this.lastKnownName == null) {
            if(bukkit != null) {
                this.lastKnownName = bukkit.getName();
            } else {
                try {
                    Logger.fine("Fetching username of [" + uniqueId + "]" );
                    this.lastKnownName = MojangApi.uuidToUsername(uniqueId).get();
                } catch (Exception e) {
                    this.lastKnownName = "Unknown";
                    Logger.severe("An error occurred while trying to fetch a player's username by UUID.");
                    e.printStackTrace();
                }
            }
        }
        return this.lastKnownName;
    }

    @Override
    public Player toBukkit() { return this.bukkit; }

    @Override
    public void setBukkit(Player bukkit) { this.bukkit = bukkit; }

    @Override
    public Location getLocation() {
        Preconditions.checkNotNull(bukkit, "player is not online");
        return bukkit.getLocation();
    }

    @Override
    public World getWorld() {
        Preconditions.checkNotNull(bukkit, "player is not online");
        return bukkit.getWorld();
    }

    @Override
    public PlayerInventory getInventory() {
        Preconditions.checkNotNull(bukkit, "player is not online");
        return bukkit.getInventory();
    }

    @Override
    public LocalMetrics getMetrics() { return this.metrics; }

    @Override
    public Kit getNullableSelectedKit() { return this.selectedKit; }

    @Override
    public boolean hasSelectedKit() {
        return this.selectedKit != null;
    }

    @Override
    public boolean setSelectedKit(Kit kit) {
        if(!isSafe()) {
            System.out.println("Not safe");
            return false;
        }
        this.selectedKit = kit;
        if(selectedKit == null) return true;

        Events.call(new KitSelectEvent(kit, this));
        if(toBukkit() == null) {
            Logger.severe("Player not found: " + (toBukkit() == null));
            return false;
        }
        final Player player = toBukkit();

        player.getInventory().clear();
        player.getActivePotionEffects().forEach(activePotion -> player.removePotionEffect(activePotion.getType()));
        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        player.setExp(0f);
        player.setLevel(0);
        if(player.isFlying()) player.setFlying(false);

        player.addPotionEffects(kit.getPotionEffects());

        player.sendMessage("§bSetting armor");
        kit.getInventoryArmors().forEach((equipmentSlot, item) -> player.getInventory().setItem(equipmentSlot, item));

        player.sendMessage("§bSetting content");
        try {
            player.getInventory().addItem(kit.getInventoryContent().toArray(new ItemStack[0]));
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public double getPoints() { return this.points.doubleValue(); }

    @Override
    public void incrementPoints(int by) { this.points.incrementAndGet(); }

    @Override
    public boolean decrementPoints(int by) {
        if(by < 0) return false;
        if(by > points.get()) return false;
        points.set(points.get() - by);
        return true;
    }

    @Override
    public boolean atArena() { return this.atArena; }

    @Override
    public void setAtArena(boolean bool) { this.atArena = bool; }

    @Override
    public boolean isSafe() { return this.safe; }

    @Override
    public void setSafeStatus(boolean status) { this.safe = status; }

    @Override
    public void updateSafeStatus() {

    }

    @Override
    public boolean hasAccess(Kit kit) {
        if(kit == null || bukkit == null) return false;
        return bukkit.hasPermission("lonkskit.kit" + kit.getBackendName());
    }

    @Override
    public void openKitSelector() {
        Preconditions.checkNotNull(bukkit, "player is not online");
        new KitSelectorGUI(this).open();
    }

    @Override
    public void msg(String message) {
        if(message == null || toBukkit() == null) return;
        toBukkit().sendMessage(Text.colorize(message));
    }

    @Override
    public void msg(Iterable<String> messages) {
        messages.forEach(this::msg);
    }

    @Override
    public void msg(String message, Object... replacements) {
        msg(Replaceable.handle(message, replacements));
    }


    @Override
    public List<LocalRent> getRents() { return this.rents; }

    @Override
    public Optional<LocalRent> getRent(Kit kit) {
        return this.rents.stream().filter(localRent -> localRent.getRented().equals(kit)).findFirst();
    }

    @Override
    public boolean hasRented(Kit kit) {
        return this.rents.stream().map(LocalRent::getRented).anyMatch(k -> k.equals(kit));
    }

    @Override
    public void addRent(LocalRent rent) {
        this.rents.add(rent);
    }

    @Override
    public void removeRent(LocalRent rent) {
        this.rents.remove(rent);
    }

    @Override
    public JsonElement serialize() {
        return JsonBuilder.object()
                .add("unique-id", uniqueId.toString())
                .add("points", points.get())

                .add("metrics", JsonBuilder.object()
                        .add("deaths", metrics.getDeathsCount())
                        .add("kills", metrics.getKillsCount())
                        .build()
                )
                .add("rents", JsonBuilder.array()
                        .addAll(
                                rents.stream().map(rent -> JsonBuilder.object()
                                        .add("uses", rent.getUses())
                                        .add("kit", rent.getRented().getBackendName())
                                        .build()
                                ).collect(Collectors.toList())
                        ).build()
                )
                .build();
    }
}
