package me.aiglez.lonkskit.abilities.itembased;

import me.aiglez.lonkskit.WorldProvider;
import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.MetadataProvider;
import me.aiglez.lonkskit.utils.items.ItemStackBuilder;
import me.lucko.helper.config.ConfigurationNode;
import me.lucko.helper.metadata.Metadata;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftCreature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author AigleZ
 * @date 09/10/2020
 */
public class NecromancerAbility extends ItemStackAbility {

    private final ItemStack item;

    public NecromancerAbility(ConfigurationNode configuration) {
        super("necromancer", configuration);
        this.item = ItemStackBuilder.of(Material.BONE)
                .name(configuration.getNode("item-name").getString("Scepter"))
                .build();
    }

    @Override
    public ItemStack getItemStack() { return this.item; }

    @Override
    public boolean isItemStack(ItemStack item) { return this.item.isSimilar(item); }

    @Override
    public void whenRightClicked(PlayerInteractEvent e) {
        e.setCancelled(true);
        final LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());

        final Zombie zombie = (Zombie) WorldProvider.KP_WORLD.spawnEntity(localPlayer.getLocation(), EntityType.SKELETON);

        zombie.setCustomName(ChatColor.AQUA + localPlayer.getLastKnownName() + "'s zombie");
        zombie.setConversionTime(-1);
        zombie.setArmsRaised(true);
        zombie.setShouldBurnInDay(false);

        ((CraftCreature) zombie).getHandle().getNavigation().d(false);

        Metadata.provideForEntity(zombie).put(MetadataProvider.NECROMANCER_ENTITY, localPlayer);

    }

    @Override
    public void whenLeftClicked(PlayerInteractEvent e) {
        e.setCancelled(true);
    }

    @Override
    public void handleListeners() {

    }
}
