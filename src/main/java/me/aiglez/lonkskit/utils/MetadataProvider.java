package me.aiglez.lonkskit.utils;

import me.aiglez.lonkskit.players.LocalPlayer;
import me.lucko.helper.metadata.MetadataKey;

public class MetadataProvider {

    /**
     * Players MetadataProvider
     */
    public static final MetadataKey<Boolean> PLAYER_NO_FALL_DAMAGE = MetadataKey.createBooleanKey("no-fall-damage");
    public static final MetadataKey<Boolean> PLAYER_NO_LIGHTING_DAMAGE = MetadataKey.createBooleanKey("no-lightning-damage");

    /**
     * Entities MetadataProvider
     */
    public static final MetadataKey<Boolean> SNOWBALL_EXPLODE = MetadataKey.createBooleanKey("snowball-explode");
    public static final MetadataKey<Boolean> HORSE_PERSISTENT = MetadataKey.createBooleanKey("persistent-horse");
    public static final MetadataKey<LocalPlayer> NECROMANCER_ENTITY = MetadataKey.create("necromancer-entity", LocalPlayer.class);

    private MetadataProvider() {
        throw new UnsupportedOperationException();
    }
}
