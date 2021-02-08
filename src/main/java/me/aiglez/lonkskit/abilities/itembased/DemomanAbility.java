package me.aiglez.lonkskit.abilities.itembased;

import lombok.Getter;
import me.aiglez.lonkskit.WorldProvider;
import me.aiglez.lonkskit.abilities.Ability;
import me.aiglez.lonkskit.abilities.AbilityPredicates;
import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.aiglez.lonkskit.utils.MetadataProvider;
import me.lucko.helper.Events;
import me.lucko.helper.config.yaml.YAMLConfigurationLoader;
import me.lucko.helper.metadata.Metadata;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;

import java.io.IOException;
import java.util.*;

public class DemomanAbility extends ItemStackAbility {
    public DemomanAbility(YAMLConfigurationLoader configurationLoader) throws IOException {
        super("demoman", configurationLoader);

    }

    @Override
    public void whenRightClicked(PlayerInteractEvent e) { }

    @Override
    public void whenLeftClicked(PlayerInteractEvent e) { }

    @Override
    public void registerListeners() {
        Events.subscribe(PlayerInteractEvent.class)
                .filter(e -> e.getAction() == Action.PHYSICAL)
                .filter(e -> e.getClickedBlock() != null)
                .filter(e -> Metadata.provideForBlock(e.getClickedBlock()).has(MetadataProvider.DEMO_BLOCK))
                .handler(e -> {
                    e.getClickedBlock().getWorld().createExplosion(e.getClickedBlock().getLocation(),4F,false,false);
                    Metadata.provideForBlock(e.getClickedBlock()).remove(MetadataProvider.DEMO_BLOCK);

                });

    }

}
