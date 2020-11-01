package me.aiglez.lonkskit.players.impl;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import me.aiglez.lonkskit.Constants;
import me.aiglez.lonkskit.WorldProvider;
import me.aiglez.lonkskit.controllers.Controllers;
import me.aiglez.lonkskit.events.KitSelectEvent;
import me.aiglez.lonkskit.kits.Kit;
import me.aiglez.lonkskit.kits.KitSelectorGUI;
import me.aiglez.lonkskit.messages.Messages;
import me.aiglez.lonkskit.players.LocalMetrics;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.players.LocalRent;
import me.aiglez.lonkskit.players.OfflineLocalPlayer;
import me.aiglez.lonkskit.players.messages.Replaceable;
import me.aiglez.lonkskit.struct.HotbarItemStack;
import me.aiglez.lonkskit.utils.Logger;
import me.aiglez.lonkskit.utils.MetadataProvider;
import me.lucko.helper.Events;
import me.lucko.helper.metadata.ExpiringValue;
import me.lucko.helper.metadata.Metadata;
import me.lucko.helper.metadata.MetadataMap;
import me.lucko.helper.text3.Text;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MemoryLocalPlayer implements LocalPlayer {

    private final OfflineLocalPlayer offline;

    private Player bukkit;
    private Kit selectedKit;
    private boolean safe, inArena;

    public MemoryLocalPlayer(final OfflineLocalPlayer offline, final Player bukkit) {
        Preconditions.checkNotNull(offline, "offline local player may not be null");
        Preconditions.checkNotNull(bukkit, "bukkit instance may not be null");
        Preconditions.checkArgument(offline.getUniqueId().equals(bukkit.getUniqueId()), "must be the same unique id");
        this.bukkit = bukkit;
        this.offline = offline;
        this.safe = true;
        this.inArena = false;
    }

    @Override
    public Player toBukkit() { return this.bukkit; }

    @Override
    public void setBukkit(Player bukkit) { this.bukkit = bukkit; }

    @Override
    public Location getLocation() {
        Preconditions.checkNotNull(this.bukkit, "player is offline");
        return this.bukkit.getLocation();
    }

    @Override
    public World getWorld() {
        Preconditions.checkNotNull(this.bukkit, "player is offline");
        return this.bukkit.getWorld();
    }

    @Override
    public boolean isValid() {
        return WorldProvider.inKPWorld(this);
    }

    @Override
    public PlayerInventory getInventory() {
        Preconditions.checkNotNull(this.bukkit, "player is offline");
        return this.bukkit.getInventory();
    }

    @Override
    public Kit getNullableSelectedKit() { return this.selectedKit; }

    @Override
    public boolean hasSelectedKit() {
        return this.selectedKit != null;
    }

    @Override
    public boolean setSelectedKit(Kit kit) {
        if(!isSafe()) {
            return false;
        }
        if(kit == null) {
            this.selectedKit = null;
            return true;
        }

        this.selectedKit = kit;
        Events.call(new KitSelectEvent(kit, this));
        if(!isOnline()) {
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

        player.addPotionEffects(this.selectedKit.getPotionEffects());

        this.selectedKit.getInventoryArmors().forEach((equipmentSlot, item) -> player.getInventory().setItem(equipmentSlot, item));

        try {
            player.getInventory().addItem(this.selectedKit.getInventoryContent().toArray(new ItemStack[0]));
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean inArena() { return this.inArena; }

    @Override
    public void setInArena(boolean status) { this.inArena = status; }

    @Override
    public boolean isSafe() { return this.safe; }

    @Override
    public void setSafeStatus(boolean status) { this.safe = status; }

    @Override
    public void updateSafeStatus() {
        if(isOnline() && getInventory().isEmpty()) {
            setSafeStatus(true);
            for (HotbarItemStack hotbarItem : Controllers.PLAYER.getHotbarItems()) {
                getInventory().addItem(hotbarItem.getItemStack());
            }
            msg(Messages.PLAYER_SAFESTATUS_UPDATED);
        }
    }

    @Override
    public boolean hasAccess(Kit kit) {
        if(kit == null || this.bukkit == null) return false;
        return bukkit.hasPermission("lonkskit.kit" + kit.getBackendName());
    }

    @Override
    public void openKitSelector() {
        Preconditions.checkNotNull(this.bukkit, "player is offline");
        new KitSelectorGUI(this).open();
    }

    @Override
    public Optional<LocalPlayer> getLastAttacker() {
        return Metadata.provideForPlayer(toBukkit()).get(MetadataProvider.LAST_ATTACKER);
    }

    @Override
    public void setLastAttacker(LocalPlayer localPlayer) {
        if(localPlayer == null || localPlayer.getUniqueId().equals(this.getUniqueId())) {
            Metadata.provideForPlayer(toBukkit()).remove(MetadataProvider.LAST_ATTACKER);
        } else {
            Metadata.provideForPlayer(toBukkit()).put(MetadataProvider.LAST_ATTACKER, ExpiringValue.of(localPlayer, Constants.ATTACKER_TAG_EXPIRING, TimeUnit.MINUTES));
        }
    }

    @Override
    public MetadataMap getMetadata() {
        Preconditions.checkNotNull(this.bukkit, "player is offline");
        return Metadata.provideForPlayer(this.bukkit);
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
    public void msg(Messages message, Object... replacements) {
        Preconditions.checkNotNull(message, "message may not be null");
        msg(message.getValue(), replacements);
    }

    @Override
    public UUID getUniqueId() {
        return this.offline.getUniqueId();
    }

    @Override
    public String getLastKnownName() {
        return this.offline.getLastKnownName();
    }

    @Override
    public boolean isOnline() {
        return this.offline.isOnline();
    }

    @Override
    public LocalPlayer getOnlinePlayer() {
        return this;
    }

    @Override
    public LocalMetrics getMetrics() {
        return this.offline.getMetrics();
    }

    @Override
    public int getPoints() {
        return this.offline.getPoints();
    }

    @Override
    public void incrementPoints(int amount) {
        this.offline.incrementPoints(amount);
    }

    @Override
    public boolean decrementPoints(int amount) {
        return this.offline.decrementPoints(amount);
    }

    @Override
    public List<LocalRent> getRents() {
        return this.offline.getRents();
    }

    @Override
    public Optional<LocalRent> getRent(Kit kit) {
        return this.offline.getRent(kit);
    }

    @Override
    public boolean hasRented(Kit kit) {
        return this.offline.hasRented(kit);
    }

    @Override
    public void addRent(LocalRent rent) {
        this.offline.addRent(rent);
    }

    @Override
    public void removeRent(LocalRent rent) {
        this.offline.removeRent(rent);
    }

    @Nonnull
    @Override
    public JsonElement serialize() {
        return this.offline.serialize();
    }
}
