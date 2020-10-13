package me.aiglez.lonkskit.abilities.external.itembased;

import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.items.ItemStackBuilder;
import me.lucko.helper.Events;
import me.lucko.helper.config.ConfigurationNode;
import org.bukkit.Material;
import org.bukkit.entity.Explosive;
import org.bukkit.entity.Fireball;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
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
                        localPlayer.msg("&e(ShadowBlade) &cPlease wait, {0} second(s) left", cooldown.remainingTime(localPlayer, TimeUnit.SECONDS));
                        return;
                    }
                    e.getPlayer().launchProjectile(Fireball.class);
                });
        Events.subscribe(EntityExplodeEvent.class)
                .filter(q-> q.getEntity() instanceof Fireball || q.getEntity() instanceof Explosive)
                .handler(e -> e.blockList().clear());
        Events.subscribe(EntityDamageByEntityEvent.class)
                .filter(e -> e.getDamager() instanceof Fireball)
                .handler(e-> e.setDamage(getConfiguration().getNode("damage").getDouble()));
    }
}
