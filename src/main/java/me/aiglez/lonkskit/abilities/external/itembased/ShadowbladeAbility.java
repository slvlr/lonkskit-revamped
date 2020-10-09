package me.aiglez.lonkskit.abilities.external.itembased;

import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.items.ItemStackBuilder;
import me.lucko.helper.Events;
import me.lucko.helper.config.ConfigurationNode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.type.Fire;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public class ShadowbladeAbility extends ItemStackAbility {

    private final ItemStack item;

    public ShadowbladeAbility(ConfigurationNode configuration) {
        super("shadowblade", configuration);
        this.item = ItemStackBuilder.of(Material.IRON_SWORD).build();
    }

    @Override
    public ItemStack getItemStack() {
        return this.item;
    }

    @Override
    public boolean isItemStack(ItemStack item) {
        return this.item.isSimilar(item); // this.item != item | this.item returns the field itemstack & item returns the parameter itemstack !!!!
    }

    @Override
    public void whenRightClicked(PlayerInteractEvent e) {
        //CHECK HANDLE
    }

    @Override
    public void whenLeftClicked(PlayerInteractEvent e) { }

    @Override
    public void handleListeners() {
        Events.subscribe(PlayerInteractEvent.class)
                .filter(AbilityPredicates.playerHasAbility(this))
                .filter(e -> e.getMaterial() == Material.IRON_SWORD)
                .filter(e -> e.getAction() == Action.RIGHT_CLICK_AIR ||e.getAction() == Action.RIGHT_CLICK_BLOCK)
                .handler(e -> {
                    e.setCancelled(true);
                    final LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());
                    if (!cooldown.test(localPlayer)) {
                        localPlayer.msg("&e(Sonic) &cPlease wait, {0} second(s) left", cooldown.remainingTime(localPlayer, TimeUnit.SECONDS));
                        return;
                    }
                    Fireball fireball = (Fireball) localPlayer.toBukkit().getWorld()
                            .spawnEntity(localPlayer.toBukkit().getTargetBlock((Set<Material>) null, 1)
                                    .getLocation(), EntityType.FIREBALL);
                    fireball.setIsIncendiary(false);
                    fireball.setBounce(false);
                    fireball.setVelocity(localPlayer.toBukkit().getLocation().getDirection().normalize().multiply(5));
                    fireball.setShooter(localPlayer.toBukkit());
                });
        Events.subscribe(EntityExplodeEvent.class)
                .filter(q-> q.getEntity() instanceof Fireball)
                .handler(e -> e.blockList().clear());
        Events.subscribe(EntityDamageByEntityEvent.class)
                .filter(e -> e.getDamager() instanceof Fireball)
                .handler(e-> e.setDamage(e.getDamage() * 2));
    }
}
