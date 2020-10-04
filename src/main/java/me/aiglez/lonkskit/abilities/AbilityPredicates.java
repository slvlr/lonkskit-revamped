package me.aiglez.lonkskit.abilities;

import me.aiglez.lonkskit.players.LocalPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerEvent;

import java.util.function.Predicate;

public class AbilityPredicates {

    /**
     * Look at {@see EventFilters.class}
     */

    public static <T extends PlayerEvent> Predicate<T> playerHasAbility(Ability ability) {
        return e -> {
            final LocalPlayer localPlayer = LocalPlayer.get(e.getPlayer());
            return localPlayer.hasSelectedKit() && localPlayer.getNullableSelectedKit().hasAbility(ability);
        };
    }

    public static <T extends EntityDamageEvent> Predicate<T> humanHasAbility(Ability ability) {
        return e -> {
            if(!(e.getEntity() instanceof Player)) return false;
            final LocalPlayer localPlayer = LocalPlayer.get((Player) e.getEntity());
            return localPlayer.hasSelectedKit() && localPlayer.getNullableSelectedKit().hasAbility(ability);
        };
    }
}
