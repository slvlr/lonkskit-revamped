package me.aiglez.lonkskit.abilities.external.functionnal;

import me.aiglez.lonkskit.abilities.FunctionalAbility;
import me.aiglez.lonkskit.events.KitSelectEvent;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.lucko.helper.Events;
import me.lucko.helper.config.ConfigurationNode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.util.Vector;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SpiderDAbility extends FunctionalAbility {
    private List<Player> spiders = new ArrayList<Player>();
    public SpiderDAbility(ConfigurationNode configuration) {
        super("spider", configuration);
    }

    @Override
    public void handleListeners() {
        Events.subscribe(KitSelectEvent.class)
                .filter(e -> e.getKit() != null)
                .filter(e -> e.getKit().getBackendName().toUpperCase().contains("SPIDER"))
                .handler(e -> {
                    DisguiseAPI.disguiseToAll(e.getLocalPlayer().toBukkit(), new MobDisguise(DisguiseType.SPIDER));
                    spiders.add(e.getLocalPlayer().toBukkit());
                });

        Events.subscribe(PlayerMoveEvent.class)
                .filter(e -> spiders.contains(e.getPlayer()))
                .handler(e -> {
                    Vector velocity = e.getPlayer().getVelocity();
                    if (Objects.requireNonNull(e.getTo()).getBlock().getType() == Material.COBWEB || e.getTo().getBlock().getType() == Material.COBWEB) {
                        e.getPlayer().setVelocity(velocity.multiply(2));
                        e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED,200,2));
                        assert PotionType.STRENGTH.getEffectType() != null;
                        e.getPlayer().addPotionEffect(new PotionEffect(PotionType.STRENGTH.getEffectType(), 200,2));
                    }else
                        e.getPlayer().setVelocity(velocity);
                });
    }
}
