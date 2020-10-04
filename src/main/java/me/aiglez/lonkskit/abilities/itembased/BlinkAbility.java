package me.aiglez.lonkskit.abilities.itembased;

import com.google.common.collect.Sets;
import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.items.ItemStackBuilder;
import me.lucko.helper.config.ConfigurationNode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author AigleZ
 * @date 03/10/2020 at 22:33
 */
public class BlinkAbility extends ItemStackAbility {

    private final ItemStack item;
    private final Set<Material> transparentMaterials = Sets.newHashSet(Material.AIR, Material.CAVE_AIR, Material.VOID_AIR);

    public BlinkAbility(ConfigurationNode configuration) {
        super("blink", configuration);
        this.item = ItemStackBuilder.of(Material.REDSTONE_TORCH)
                .name("&bBlinker")
                .build();
    }

    @Override
    public ItemStack getItemStack() { return this.item; }

    @Override
    public boolean isItemStack(ItemStack item) { return this.item.isSimilar(item); }

    // --------------------------------------------------------------------------------------------
    @Override
    public void whenUsed(PlayerInteractEvent e) {
        e.setCancelled(true);
        final LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());
        final Block target = localPlayer.toBukkit().getTargetBlock(transparentMaterials, configuration.getNode("max-radius").getInt(100));

        if(!cooldown.test(localPlayer)){
            localPlayer.msg("&9(Blink) &cPlease wait, {0} second(s) left", cooldown.remainingTime(localPlayer, TimeUnit.SECONDS));
            return;
        }

        localPlayer.toBukkit().teleport(target.getLocation().clone().add(0D, 1D, 0D));
        localPlayer.msg("&9(Blink) &aYou have blinked!");
    }

    @Override
    public void handleListeners() {
    }
}
