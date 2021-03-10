package me.aiglez.lonkskit.abilities.itembased;

import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.FunctionalAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.config.yaml.YAMLConfigurationLoader;
import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Assassin extends FunctionalAbility {
    static final List<LocalPlayer> list = new ArrayList<>();
    public Assassin(YAMLConfigurationLoader configurationLoader) throws IOException {
        super("assassin", configurationLoader);
    }

    @Override
    public void registerListeners() {
        Events.subscribe(PlayerToggleSneakEvent.class)
                .filter(AbilityPredicates.hasAbility(this))
                .filter(PlayerToggleSneakEvent::isSneaking)
                .handler(e -> {
                        LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());
                        if (list.contains(localPlayer)) return;
                        AtomicInteger i = new AtomicInteger(getConfiguration().getNode("charge-time").getInt());
                        Schedulers.sync().runRepeating(t -> {
                            if (localPlayer.toBukkit().isSneaking()) {
                                if (i.get() < 0) {
                                    applyEffects(localPlayer);
                                    localPlayer.msg("&aPotion effects Activated!");
                                    t.stop();
                                    t.close();
                                } else {
                                    localPlayer.toBukkit().sendTitle(ChatColor.DARK_PURPLE + "" + (i.get() + 1) + " Seconds Left !","",5,20,5);
                                }
                            }else {
                                t.stop();
                                t.close();
                            }
                            i.decrementAndGet();
                        },0L,20L);
                });
    }
}
