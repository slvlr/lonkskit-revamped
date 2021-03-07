package me.aiglez.lonkskit.abilities.helpers;

import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityTargetEvent;

import java.util.EnumSet;

public class CowboyHelper extends EntityHorse {
    public CowboyHelper(Location location, Player player) {
        super(EntityTypes.HORSE,((CraftWorld)location.getWorld()).getHandle());
        this.setPosition(location.getX(),location.getY(),location.getZ());
        this.setInvulnerable(true);
        this.setGoalTarget(((CraftPlayer)player).getHandle(), EntityTargetEvent.TargetReason.CUSTOM,true);
//        Horse horse = (Horse) this.getBukkitEntity().getHandle();
//        horse.getInventory().setSaddle(new ItemStack(org.bukkit.Material.SADDLE, 1));
//        horse.getInventory().setArmor(new org.bukkit.inventory.ItemStack(Material.DIAMOND_HORSE_ARMOR, 1));
//        this.getBukkitEntity().getHandle().getBukkitEntity();
//
//        horse.setTamed(true);
//        horse.setOwner(player);
//
//        horse.addPassenger(player);
    }

    @Override
    protected void initPathfinder() {
        this.goalSelector.a(0,new PathfinderGoalFloat(this));
        this.goalSelector.a(2,new PathfinderGoalLookAtPlayer(this,EntityHuman.class,8.0F));
        this.goalSelector.a(1,new PathFinderGoalPet(this,3.5D,12F));

    }
}
class PathFinderGoalPet extends PathfinderGoal {

    private final EntityInsentient a;
    private EntityLiving b;
    private final double f;
    private final float g; //Distance between owner & pet
    private double c;
    private double d;
    private double e;

    PathFinderGoalPet(EntityInsentient a, double speed, float distance) {
        this.a = a;
        this.f = speed;
        this.g = distance;
        this.a(EnumSet.of(Type.MOVE, Type.LOOK));
    }

    @Override
    public boolean a() {
        this.b = this.a.getGoalTarget();
        if (this.b == null) return false;
        else if (this.a.getDisplayName() == null) return false;
        else if (!(this.a.getDisplayName().toString().contains(this.b.getName()))) return false;
        else if (this.b.h(this.a) > (double) (this.g * this.g)){
            a.setPosition(this.b.locX(),this.b.locY(),this.b.locZ());
            return false;
        }
        else {
            Vec3D vec3D = RandomPositionGenerator.a((EntityCreature) this.a,16,7,this.b.getPositionVector());
            if (vec3D == null) return false;
            this.c = this.b.locX();
            this.d = this.b.locX();
            this.e = this.b.locZ();
            return true;
        }
    }

    @Override
    public void c() {
        this.a.getNavigation().a(this.c,this.d,this.e,this.f);
    }

    @Override
    public boolean b() {
        return !this.a.getNavigation().m() && this.b.h(this.a) < (double) (this.g * this.g);

    }

    @Override
    public void d() {
        this.b = null;

    }
}