package me.aiglez.lonkskit.abilities.itembased

import me.aiglez.lonkskit.WorldProvider
import me.aiglez.lonkskit.abilities.ItemStackAbility
import me.aiglez.lonkskit.players.LocalPlayer
import me.aiglez.lonkskit.utils.MetadataProvider
import me.lucko.helper.Events
import me.lucko.helper.Schedulers
import me.lucko.helper.config.yaml.YAMLConfigurationLoader
import me.lucko.helper.metadata.Metadata
import org.bukkit.entity.Firework
import org.bukkit.event.entity.FireworkExplodeEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.player.PlayerInteractEvent
import java.util.concurrent.TimeUnit
import kotlin.random.Random
import org.bukkit.FireworkEffect

import org.bukkit.inventory.meta.FireworkMeta

import me.lucko.helper.Helper.world

import org.bukkit.Bukkit
import org.bukkit.entity.Arrow
import org.bukkit.entity.EntityType
import org.bukkit.entity.Fireball
import kotlin.concurrent.timerTask


class MortarAbility(configurationLoader : YAMLConfigurationLoader) : ItemStackAbility("mortar",configurationLoader) {

    override fun registerListeners() {
        Events.subscribe(ProjectileHitEvent::class.java)
            .filter { e -> Metadata.provideForEntity(e.entity).has(MetadataProvider.FIREWORK_EXPLODE)}
            .handler { e ->
                run {
                    Schedulers.sync()
                        .runLater({ e.hitBlock?.location?.let { e.hitBlock?.world?.createExplosion(it,4F,true) } },4,TimeUnit.SECONDS)
                }
            }
    }

    override fun whenRightClicked(e: PlayerInteractEvent) {
        val localPlayer = LocalPlayer.get(e.player)
        if (!this.cooldown.test(localPlayer)){
            localPlayer.msg("&b[LonksKit] &cPlease wait, {0} second(s) left", cooldown.remainingTime(localPlayer, TimeUnit.SECONDS))
            return
        }
        val loc = localPlayer.toBukkit().eyeLocation
        val v = loc.direction
        v.setY(0)
        val speed = 0.5
        v.normalize().multiply(speed)
        /*val firework = loc.world.spawn(loc,Firework::class.java)
        val ent = true
        Schedulers.sync().runRepeating({to ->
            run {
                firework.ticksLived = 1
                loc.add(v)
                firework.teleport(loc)
                if (firework.isOnGround){
                    firework.teleport(loc.subtract(v))
                    firework.detonate()
                    to.close()
                }
            }
        },0,1)*/
        val ball = localPlayer.toBukkit().launchProjectile(Arrow::class.java)




        // final Location loc = event.getPlayer().getEyeLocation();
        //        final Vector v = loc.getDirection();
        //        v.setY(0); // if you want it to go horizontally, if you want it to go the direction the player is looking then remove this line
        //        double speed = 0.5;
        //        v.normalize().multiply(speed); // The speed is how fast it should move, 1 = one block per tick, values over 1 might cause the firework to go through 1 thick block walls
        //        final Firework f = loc.getWorld().spawn(loc, Firework.class);
        //
        //        /*
        //         * Do all your firework effect stuff here (e.g. if it should be a star, colors etc)
        //         */
        //
        //        final boolean ent = true; // Also check if we hit entities
        //
        //        new BukkitRunnable(){
        //            public void run(){
        //                f.setTicksLived(0); // To prevent it from detonating before we want it to
        //                loc.add(v);
        //                f.teleport(loc);
        //                if(f.isOnGround()){ // We have hit a solid block, but are now inside that block
        //                    f.teleport(loc.subtract(v)); // Go back one step to get outside the block
        //                    f.detonate();
        //                    this.cancel(); // Stops the task as we are now done
        //                }
        //
        //                if(ent){ // Check if we have hit entities
        //                    Vec3D v3 = new Vec3D(loc.getX(), loc.getY(), loc.getZ()); // Create a nms vector representing the firework's location
        //                    for(Entity e : f.getNearbyEntities(2, 2, 2)){ // Scan nearby entities
        //                        if(((CraftEntity)e).getHandle().getBoundingBox().a(v3)){ // Check if the firework is inside the entity's hitbox/bounding box
        //                            // We have hit an entity, detonate and exit (return prevents the for loop form continuing as we are now done
        //                            f.detonate();
        //                            this.cancel();
        //                            return;
        //                        }
        //                    }
        //
        //                }
        //
        //            }
        //        }.runTaskTimer(plugin, 0, 1); // Create a task to run every tick (plugin = your plugin instance)



    }

    override fun whenLeftClicked(e: PlayerInteractEvent?) {
        return
    }

    private fun getVariable(x: Float, y: Float): Float {
        return Random.nextDouble(x.toDouble(),y.toDouble()).toFloat()
    }
}