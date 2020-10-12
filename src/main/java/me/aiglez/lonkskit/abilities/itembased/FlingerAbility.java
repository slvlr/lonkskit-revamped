package me.aiglez.lonkskit.abilities.itembased;

import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.MetadataProvider;
import me.aiglez.lonkskit.utils.items.ItemStackBuilder;
import me.lucko.helper.Events;
import me.lucko.helper.config.ConfigurationNode;
import me.lucko.helper.metadata.Metadata;
import org.bukkit.Material;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class FlingerAbility extends ItemStackAbility {

    private final ItemStack item;

    public FlingerAbility(ConfigurationNode configuration) {
        super("flinger", configuration);
        this.item = ItemStackBuilder.of(Material.WOODEN_HOE)
                .name(configuration.getNode("item-name").getString("Flinger"))
                .build();
    }

    @Override
    public ItemStack getItemStack() {
        return this.item;
    }

    @Override
    public boolean isItemStack(ItemStack item) {
        return this.item.isSimilar(item);
    }

    @Override
    public void whenRightClicked(PlayerInteractEvent e) {
        e.setCancelled(true);
        final LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());

        final Egg egg = localPlayer.toBukkit().launchProjectile(Egg.class);
        egg.setShooter(localPlayer.toBukkit());
        Metadata.provideForEntity(egg).put(MetadataProvider.EGG_FLINGER, true);
    }

    @Override
    public void whenLeftClicked(PlayerInteractEvent e) {
        e.setCancelled(true);
    }

    @Override
    public void handleListeners() {
        Events.subscribe(EntityDamageByEntityEvent.class)
                .filter(e -> e.getDamager() instanceof Egg)
                .filter(e -> Metadata.provideForEntity(e.getDamager()).has(MetadataProvider.EGG_FLINGER))
                .handler(e -> {
                    final Egg damager = (Egg) e.getDamager();
                    final Entity victim = e.getEntity();

                    if (damager.getShooter() == null) return;

                    final LocalPlayer shooter = LocalPlayer.get((Player) damager.getShooter());
                    if (shooter.toBukkit() == null) return;

                    if (victim.getUniqueId().equals(shooter.getUniqueId())) {
                        shooter.msg("&6(Flinger - Debug) &cYou can't target yourself!");
                        return;
                    }

                    double distance = shooter.getLocation().distance(victim.getLocation()); //TODO: cleanup later
                    if(distance > 20) {
                        e.setDamage(999);
                        shooter.msg("&6(Flinger - Debug) &aYou have instant-killed {0}", victim.getName());
                    } else {
                        e.setDamage(2D);
                        shooter.msg("&6(Flinger - Debug) &cOne heart damage ({0} < 20)" , distance);
                    }

                    damager.remove();
                });
    }

}