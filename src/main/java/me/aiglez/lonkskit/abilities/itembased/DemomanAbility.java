package me.aiglez.lonkskit.abilities.itembased;

import me.aiglez.lonkskit.abilities.ItemStackAbility;
import me.aiglez.lonkskit.players.LocalPlayer;
import me.lucko.helper.Events;
import me.lucko.helper.config.yaml.YAMLConfigurationLoader;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.IOException;
import java.util.EnumSet;

public class DemomanAbility extends ItemStackAbility {

    private final EnumSet<Material> validMaterials;

    public DemomanAbility(YAMLConfigurationLoader configurationLoader) throws IOException {
        super("demoman", configurationLoader);
        this.validMaterials = EnumSet.of(
                Material.GRASS, Material.END_STONE, Material.NETHER_BRICK,
                Material.MYCELIUM, Material.NETHERRACK, Material.SOUL_SAND, Material.OBSIDIAN,
                Material.SAND, Material.DIRT, Material.SPONGE
        );
    }

    @Override
    public void whenRightClicked(PlayerInteractEvent e) { }

    @Override
    public void whenLeftClicked(PlayerInteractEvent e) { }

    @Override
    public void registerListeners() {
        Events.subscribe(BlockPlaceEvent.class)
                .filter(e -> {
                    final LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());
                    return localPlayer.hasSelectedKit() && localPlayer.getNullableSelectedKit().hasAbility(this);
                })
                .filter(e -> isItemStack(e.getItemInHand()))
                .handler(e -> {
                    final LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());
                    final Block block = e.getBlock();



                });
    }

    private boolean isValid(Block block) {
        return validMaterials.contains(block.getType());
    }
}
