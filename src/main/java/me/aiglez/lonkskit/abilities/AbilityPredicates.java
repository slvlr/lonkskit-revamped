package me.aiglez.lonkskit.abilities;

import me.aiglez.lonkskit.players.LocalPlayer;
import me.lucko.helper.metadata.Metadata;
import me.lucko.helper.metadata.MetadataKey;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.player.PlayerEvent;

import java.util.function.Predicate;

public class AbilityPredicates {

    // -------------------------------------------- //
    // ABILITIES
    // -------------------------------------------- //
    public static <T extends PlayerEvent> Predicate<T> playerHasAbility(Ability ability) {
        return e -> {
            final LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());
            return localPlayer.hasSelectedKit() && localPlayer.getNullableSelectedKit().hasAbility(ability);
        };
    }

    public static <T extends EntityEvent> Predicate<T> humanHasAbility(Ability ability) {
        return e -> {
            if(!(e.getEntity() instanceof Player)) return false;
            final LocalPlayer localPlayer = LocalPlayer.get((Player) e.getEntity());
            return localPlayer.hasSelectedKit() && localPlayer.getNullableSelectedKit().hasAbility(ability);
        };
    }
    // -------------------------------------------- //
    // METADATA
    // -------------------------------------------- //
    public static <T extends PlayerEvent> Predicate<T> playerHasMetadata(MetadataKey<?> metadataKey) {
        return e -> Metadata.provideForPlayer(e.getPlayer()).has(metadataKey);
    }

    public static <T extends EntityEvent> Predicate<T> humanHasMetadata(MetadataKey<?> metadataKey) {
        return e -> {
            if(!(e.getEntity() instanceof Player)) return false;
            return Metadata.provideForPlayer((Player) e.getEntity()).has(metadataKey);
        };
    }

    // -------------------------------------------- //
    // MISC
    // -------------------------------------------- //
}
