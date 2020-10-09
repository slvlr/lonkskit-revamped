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
        this.goalSelector.a(1, new PathfinderGoalFloat(this));
        this.goalSelector.a(2, new PathfinderGoalSit(this));
        this.goalSelector.a(4, new PathfinderGoalLeapAtTarget(this, 0.4F));
        this.goalSelector.a(5, new PathfinderGoalMeleeAttack(this, 1.0D, true));
        this.goalSelector.a(6, new PathfinderGoalFollowOwner(this, 1.0D, 10.0F, 2.0F, false));
        this.goalSelector.a(8, new PathfinderGoalRandomStrollLand(this, 1.0D));
        this.goalSelector.a(10, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(10, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(1, new PathfinderGoalOwnerHurtByTarget(this));
        this.targetSelector.a(2, new PathfinderGoalOwnerHurtTarget(this));
        this.targetSelector.a(3, (new PathfinderGoalHurtByTarget(this)).a(new Class[0]));
        this.targetSelector.a(4, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, 10, true, false, this::a_));
        this.targetSelector.a(8, new PathfinderGoalUniversalAngerReset<>(this, true));
    }
}
