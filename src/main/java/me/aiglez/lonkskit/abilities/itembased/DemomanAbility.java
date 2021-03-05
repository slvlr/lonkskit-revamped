package me.aiglez.lonkskit.abilities.itembased;

import com.google.common.reflect.TypeToken;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.listeners.FeaturesListeners;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.config.yaml.YAMLConfigurationLoader;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class DemomanAbility extends ItemStackAbility {
    @Getter
    @Setter
    private static Set<Material> interdit = new HashSet<>();
    @Getter
    public static DemomanAbility INSTANCE;
    @SneakyThrows
    public DemomanAbility(YAMLConfigurationLoader configurationLoader) throws IOException {
        super("demoman", configurationLoader);
        interdit.addAll(new ArrayList<>(this.configuration.getNode("blocks").getList(TypeToken.of(String.class)).stream().map(Material::matchMaterial).collect(Collectors.toList())));
        INSTANCE = this;
        System.out.println(interdit);
    }

    @Override
    public void whenRightClicked(PlayerInteractEvent e) { }

    @Override
    public void whenLeftClicked(PlayerInteractEvent e) { }

    @Override
    public void registerListeners() {
        Events.subscribe(PlayerInteractEvent.class)
                .filter(e -> e.getAction() == Action.PHYSICAL)
                .filter(e -> FeaturesListeners.demoBlocks.containsKey(e.getClickedBlock()))
                .handler(e -> {
                    e.getPlayer().sendMessage("you are here finally");
                    Objects.requireNonNull(e.getClickedBlock()).getWorld().createExplosion(e.getClickedBlock().getLocation(),4F,false,false);
                    LocalPlayer killer = FeaturesListeners.demoBlocks.get(e.getClickedBlock());
                    killer.msg("&b[DEBUG] &cYou have entered in combat with {0}.", LocalPlayer.get(e.getPlayer()).getLastKnownName());
                    LocalPlayer.get(e.getPlayer()).msg("&b[DEBUG] &cYou have entered in combat with {0}.", killer.getLastKnownName());
                    FeaturesListeners.demoBlocks.remove(e.getClickedBlock());
                    Schedulers.sync().runLater(() -> {
                        e.getClickedBlock().breakNaturally();
                        e.getClickedBlock().setBlockData(Material.AIR.createBlockData());
                        Schedulers.sync().runLater(() -> e.getClickedBlock().getState().update(true),2L);
                    },3L);

                });

    }

}
