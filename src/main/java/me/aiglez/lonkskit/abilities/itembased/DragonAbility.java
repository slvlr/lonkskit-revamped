package me.aiglez.lonkskit.abilities.itembased;

import me.aiglez.lonkskit.WorldProvider;
import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.Metadatas;
import me.aiglez.lonkskit.utils.items.ItemStackBuilder;
import me.lucko.helper.Schedulers;
import me.lucko.helper.config.ConfigurationNode;
import me.lucko.helper.metadata.ExpiringValue;
import me.lucko.helper.scheduler.Ticks;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.concurrent.TimeUnit;

/**
 * @author AigleZ
 * @date 01/10/2020 at 13:50
 */
public class DragonAbility extends ItemStackAbility {

    private final ItemStack item;
    private final double strength;

    public DragonAbility(ConfigurationNode configuration) {
        super("dragon", configuration);
        this.item = ItemStackBuilder.of(Material.FEATHER)
                .name("&bBoost")
                .enchant(Enchantment.DURABILITY, 1)
                .build();
        this.strength = configuration.getNode("strength").getDouble(5D);
    }

    @Override
    public ItemStack getItemStack() { return this.item; }

    @Override
    public boolean isItemStack(ItemStack item) { return this.item.isSimilar(item); }

    // --------------------------------------------------------------------------------------------
    @Override
    public void whenClicked(PlayerInteractEvent e) {
        e.setCancelled(true);
        final LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());
        if(!cooldown.test(localPlayer)){
            localPlayer.msg("&cPlease wait, {0} second(s) left", cooldown.remainingTime(localPlayer, TimeUnit.SECONDS));
            return;
        }

        localPlayer.metadata().put(Metadatas.PLAYER_NO_FALL_DAMAGE, ExpiringValue.of(true, 10, TimeUnit.SECONDS));

        final Vector vec = localPlayer.toBukkit().getLocation().getDirection().multiply(strength);
        localPlayer.toBukkit().setVelocity(vec);

        Schedulers.sync()
                .runRepeating(() -> {
                    WorldProvider.KP_WORLD.playEffect(localPlayer.getLocation(), Effect.MOBSPAWNER_FLAMES, 0);
                }, 1L, Ticks.from(4, TimeUnit.SECONDS));

        localPlayer.msg("&b[Debug] &fYou have been pushed (strength: {0})", strength);
    }
}
