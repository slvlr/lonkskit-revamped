package me.aiglez.lonkskit.utils;

import me.aiglez.lonkskit.players.LocalPlayer;
import me.lucko.helper.metadata.MetadataKey;

public class MetadataProvider {

    /**
     * Players MetadataProvider
     */

    public static final MetadataKey<Boolean> PLAYER_NO_FALL_DAMAGE = MetadataKey.createBooleanKey("no-fall-damage");
    public static final MetadataKey<Boolean> PLAYER_NO_LIGHTING_DAMAGE = MetadataKey.createBooleanKey("no-lightning-damage");
    public static final MetadataKey<Boolean> PLAYER_DOUBLE_DAMAGE = MetadataKey.createBooleanKey("double-damage");
    public static final MetadataKey<Boolean> SPY_PLAYER = MetadataKey.createBooleanKey("spy-player");
    public static final MetadataKey<Boolean> LEAVE_HORSE = MetadataKey.createBooleanKey("spy-player");

    public static final MetadataKey<LocalPlayer> LAST_ATTACKER = MetadataKey.create("combat-tag", LocalPlayer.class);
    /**
     * Entities MetadataProvider
     */
    public static final MetadataKey<Boolean> DEMO_BLOCK = MetadataKey.createBooleanKey("demo-block");
    public static final MetadataKey<Boolean> SPARTAN_STICK = MetadataKey.createBooleanKey("spartan-stick");
    public static final MetadataKey<Boolean> SNOWMAN = MetadataKey.createBooleanKey("snowman-ball");
    public static final MetadataKey<Boolean> KILLER_TNT =   MetadataKey.createBooleanKey("killer-tnt");
    public static final MetadataKey<Boolean> KAMIKAZ =   MetadataKey.createBooleanKey("kamikaze-tnt");
    public static final MetadataKey<Boolean> SNOWBALL_EXPLODE = MetadataKey.createBooleanKey("snowball-explode");
    public static final MetadataKey<LocalPlayer> HORSE_PERSISTENT = MetadataKey.create("persistent-horse", LocalPlayer.class);
    public static final MetadataKey<LocalPlayer> NECROMANCER_ENTITY = MetadataKey.create("necromancer-entity", LocalPlayer.class);
    public static final MetadataKey<Boolean> SPY_ARROW = MetadataKey.createBooleanKey("spy-arrow");
    public static final MetadataKey<Boolean> HULK_PICKED_UP = MetadataKey.createBooleanKey("hulk-pic");
    public static final MetadataKey<Boolean> EGG_FLINGER = MetadataKey.createBooleanKey("egg-flinger");
    public static final MetadataKey<Boolean> SHADOW_FIREBALL = MetadataKey.createBooleanKey("shadow-fireball");

    private MetadataProvider() {
        throw new UnsupportedOperationException();
    }
}
