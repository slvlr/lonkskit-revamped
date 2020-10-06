package me.aiglez.lonkskit.utils;

import me.lucko.helper.metadata.MetadataKey;

public class MetadataProvider {

    /**
     * Players MetadataProvider
     */
    public static final MetadataKey<Boolean> PLAYER_NO_FALL_DAMAGE = MetadataKey.createBooleanKey("no-fall-damage");
    public static final MetadataKey<Boolean> PLAYER_NO_LIGHTING_DAMAGE = MetadataKey.createBooleanKey("no-lightning-damage");

    public static final MetadataKey<Boolean> HORSE_PERSISTENT = MetadataKey.createBooleanKey("persistent-horse");

    /**
     * Entities MetadataProvider
     */
    public static final MetadataKey<Boolean> SNOWBALL_EXPLODE = MetadataKey.createBooleanKey("snowball-explode");

    private MetadataProvider() {
        throw new UnsupportedOperationException();
    }
}
