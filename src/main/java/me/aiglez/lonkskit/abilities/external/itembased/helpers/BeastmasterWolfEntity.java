package me.aiglez.lonkskit.abilities.external.itembased.helpers;

import me.aiglez.lonkskit.utils.Logger;
import net.minecraft.server.v1_16_R2.*;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R2.CraftWorld;
import org.bukkit.entity.Player;

public class BeastmasterWolfEntity extends EntityWolf {

    private final Player owner;

    public BeastmasterWolfEntity(Player owner, World world) {
        super(EntityTypes.WOLF, ((CraftWorld) world).getHandle());
        Logger.debug("Spawning a beastmaster wolf... (owner: " + owner.getName() + ")");
        this.owner = owner;

        setTamed(true);
        setOwnerUUID(owner.getUniqueId());
        setCustomName(new ChatComponentText(owner.getName() + "'s wolf"));
        this.setCustomNameVisible(true);

    }

    @Override
    protected void initPathfinder() {

    }
}
