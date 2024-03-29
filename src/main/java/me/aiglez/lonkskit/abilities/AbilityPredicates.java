package me.aiglez.lonkskit.abilities;

import me.aiglez.lonkskit.players.LocalPlayer;
import me.lucko.helper.metadata.Metadata;
import me.lucko.helper.metadata.MetadataKey;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.player.PlayerEvent;

import java.util.function.Predicate;

public class AbilityPredicates {

    // -------------------------------------------- //
    // ABILITIES
    // -------------------------------------------- //
    public static <T extends PlayerEvent> Predicate<T> hasAbility(Ability ability) {
        return e -> {
            final LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());
            return localPlayer.hasSelectedKit() && localPlayer.getNullableSelectedKit().hasAbility(ability);
        };
    }


    public static <T extends EntityDeathEvent> Predicate<T> hasAbilityV(Ability ability) {
        return e -> {
            final LocalPlayer localPlayer = LocalPlayer.get((Player) e.getEntity());
            return localPlayer.hasSelectedKit() && localPlayer.getNullableSelectedKit().hasAbility(ability);
        };
    }


    public static <T extends EntityEvent> Predicate<T> possiblyHasAbility(Ability ability) {
        return e -> {
            if(!(e.getEntity() instanceof Player)) return false;
            final LocalPlayer localPlayer = LocalPlayer.get((Player) e.getEntity());
            return localPlayer.hasSelectedKit() && localPlayer.getNullableSelectedKit().hasAbility(ability);
        };
    }

    public static <T extends EntityEvent> Predicate<T> possiblyHasAbilities(String... abilitiesName) {
        return e -> {
            if(!(e.getEntity() instanceof Player)) return false;
            final LocalPlayer localPlayer = LocalPlayer.get((Player) e.getEntity());
            if(!localPlayer.hasSelectedKit()) return false;
            for (final String abilityName : abilitiesName) {
                final Ability ability = Ability.get(abilityName);
                if(localPlayer.getNullableSelectedKit().hasAbility(ability)) {
                    return true;
                }
            }
            return false;
        };
    }


    // JOHAN START
    public static <T extends EntityEvent> Predicate<T> isKillerhaveAbility(Ability ability) {
        return e -> {
            if(!(e.getEntity() instanceof Player)) return false;
            final LocalPlayer localPlayer = LocalPlayer.get(((Player) e.getEntity()).getKiller());
            return localPlayer.hasSelectedKit() && localPlayer.getNullableSelectedKit().hasAbility(ability);
        };
    }
    public static boolean HastheKit(Ability ability,EntityDamageByEntityEvent e){
        if(!(e.getEntity() instanceof Player)) return false;
        final LocalPlayer player = LocalPlayer.get((Player) e.getDamager());
        return player.hasSelectedKit() && player.getNullableSelectedKit().hasAbility(ability);

    }

    public static  Predicate<EntityDamageByEntityEvent> damagerHasAbility(Ability ability) {
        return e -> {
            if(!(e.getDamager() instanceof Player)) return false;
            final LocalPlayer localPlayer = LocalPlayer.get(((Player) e.getDamager()));
            return localPlayer.hasSelectedKit() && localPlayer.getNullableSelectedKit().hasAbility(ability);
        };
    }

    //JOHAN END :)

    // -------------------------------------------- //
    // METADATA
    // -------------------------------------------- //
    public static <T extends PlayerEvent> Predicate<T> hasMetadata(MetadataKey<?> metadataKey) {
        return e -> Metadata.provideForPlayer(e.getPlayer()).has(metadataKey);
    }

    public static <T extends EntityEvent> Predicate<T> possiblyHasMetadata(MetadataKey<?> metadataKey) {
        return e -> {
            if(!(e.getEntity() instanceof Player)) return false;
            return Metadata.provideForPlayer((Player) e.getEntity()).has(metadataKey);
        };
    }

    public static <T extends EntityEvent> Predicate<T> entityHasMetadata(MetadataKey<?> metadataKey) {
        return e -> Metadata.provideForEntity(e.getEntity()).has(metadataKey);
    }
    // -------------------------------------------- //
    // MISC
    // -------------------------------------------- //
}
