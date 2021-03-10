package me.aiglez.lonkskit.abilities.itembased;

import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.lucko.helper.Events;
import me.lucko.helper.config.yaml.YAMLConfigurationLoader;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class MonkAbility extends ItemStackAbility {

    public MonkAbility(YAMLConfigurationLoader configurationLoader) throws IOException {
        super("monk", configurationLoader);
    }

    @Override
    public void whenRightClicked(PlayerInteractEvent e) {

    }

    @Override
    public void whenLeftClicked(PlayerInteractEvent e) {

    }

    @Override
    public void registerListeners() {
        Events.subscribe(PlayerInteractEntityEvent.class)
                .filter(AbilityPredicates.hasAbility(this))
                .filter(o -> o.getRightClicked() instanceof Player)
                .filter(e -> isItemStack(e.getPlayer().getInventory().getItemInMainHand()))
                .handler(e -> {
                    final LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());
                    if(!cooldown.test(localPlayer)){
                        LocalPlayer.get(e.getPlayer()).msg("&b[LonksKit] &cPlease wait, {0} second(s) left", cooldown.remainingTime(LocalPlayer.get(e.getPlayer()), TimeUnit.SECONDS));
                        e.setCancelled(true);
                        return;
                    }

                    applyEffects(localPlayer);

                    final Player rightClicked = (Player) e.getRightClicked();
                    int index = RandomUtils.nextInt(30); // RandomUtils is more optimized then created a new Random object each time

                    final ItemStack itemAtIndex = rightClicked.getInventory().getItem(index);
                    final ItemStack itemInMainHand = rightClicked.getInventory().getItemInMainHand(); // Player#getItemInHand is @deprecated
                    rightClicked.getInventory().setItemInMainHand(itemAtIndex);
                    rightClicked.getInventory().setItem(index, itemInMainHand);
                });
    }
}
