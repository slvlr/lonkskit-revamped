package me.aiglez.lonkskit.abilities.external.itembased;

import me.aiglez.lonkskit.KitPlugin;
import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.items.ItemStackBuilder;
import me.lucko.helper.Events;
import me.lucko.helper.config.ConfigurationNode;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

public class ElderAbility extends ItemStackAbility {
    ItemStack item;
    public ElderAbility(ConfigurationNode configuration) {
        super("elder", configuration);
        this.item = ItemStackBuilder.of(Material.BLAZE_ROD)
                .name("Staff")
                .build();
    }

    @Override
    public ItemStack getItemStack() {
        return item;
    }

    @Override
    public boolean isItemStack(ItemStack item) {
        return this.item.isSimilar(item);
    }

    @Override
    public void whenRightClicked(PlayerInteractEvent e) {


    }

    @Override
    public void whenLeftClicked(PlayerInteractEvent e) {

    }

    @Override
    public void handleListeners() {
        Events.subscribe(PlayerInteractEntityEvent.class)
                .filter(AbilityPredicates.playerHasAbility(this))
                .filter(event -> event.getRightClicked() instanceof Player)
                .handler(event -> {
                    LocalPlayer localPlayer = LocalPlayer.get(event.getPlayer());
                    Player victim = (Player) event.getRightClicked();
                    if (!cooldown.test(localPlayer)){
                        localPlayer.msg("&e(Sonic) &cPlease wait, {0} second(s) left", cooldown.remainingTime(localPlayer, TimeUnit.SECONDS));
                        return;
                    }
                    int index = RandomUtils.nextInt(4);
                    ItemStack item =  victim.getInventory().getArmorContents()[index];
                    ItemStack[] armor = victim.getInventory().getArmorContents();
                    String displayname = item.getItemMeta().getDisplayName();
                    ItemStack[] newarmor = victim.getInventory().getArmorContents();
                    for (int i = 0 ; i > armor.length ; i++){
                        if (!armor[i].getItemMeta().getDisplayName().equalsIgnoreCase(displayname)){
                            newarmor[i] = armor[i];
                        }
                    }
                    victim.getInventory().setArmorContents(newarmor);
                    Bukkit.getScheduler().runTaskLater(KitPlugin.getSingleton(), new Runnable() {
                        @Override
                        public void run() {
                            victim.getInventory().setArmorContents(armor);
                        }
                    },200L);

                });


    }
}
/*Elder: Leatherbody, leather leggings, leather boots <-- all Protection III. Woodsword:Unbreaking III Sharpness II. Blaze rod. 34 soup.
(RENAME blaze rod 'Staff
')(If a player right clicks another player with a blaze rod,  will remove a random piece of the targets armor for 10 seconds. 15 seconds cooldown*/