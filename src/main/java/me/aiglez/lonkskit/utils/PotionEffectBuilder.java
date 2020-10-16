package me.aiglez.lonkskit.utils;

import com.google.common.base.Preconditions;
import me.lucko.helper.scheduler.Ticks;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.TimeUnit;

public class PotionEffectBuilder {

    public static PotionEffectBuilder newBuilder() {
        return new PotionEffectBuilder();
    }

    /*
     * Parse a string to a builder.
     *
     * ex: "speed:1:1" this will return a potion effect of type SPEED, with amplifier 0
     * and will remain 1 second.
     *
     * ex2: "speed:2:**" this will return a potion effect of type SPEED
     */
    public static PotionEffectBuilder parse(String toParse) {
        Preconditions.checkNotNull(toParse, "The text to parse must not be empty.");
        Preconditions.checkArgument(!toParse.isEmpty(), "The text to parse must not be empty");

        final String[] split = toParse.split(":");
        final PotionEffectBuilder builder = newBuilder();

        String type = split[0];
        builder.type(type);
        builder.amplifier(NumberUtils.toInt(split[1], 1));
        if(split.length >= 3) {
            String duration = split[2];
            if(duration.equalsIgnoreCase("**")) {
                builder.permanent();
            } else {
                builder.duration(NumberUtils.toLong(duration, 1L));
            }
        } else {
            builder.permanent();
        }

        return builder;
    }

    private PotionEffectType type;
    private long duration = 1L;
    private int amplifier = 1;

    public PotionEffectBuilder type(PotionEffectType type) {
        this.type = type;
        return this;
    }

    public PotionEffectBuilder type(String name) {
        Preconditions.checkNotNull(name, "Type's name must no be empty.");
        final PotionEffectType type = PotionEffectType.getByName(name);
        if(type == null) {
            Logger.warn("Couldn't find any Potion Effect Type matching (" + name + ")");
            this.type = PotionEffectType.FAST_DIGGING;
            return this;
        }
        this.type = type;
        return this;
    }

    public PotionEffectBuilder permanent() {
        this.duration = Integer.MAX_VALUE;
        return this;
    }

    public PotionEffectBuilder duration(long duration) {
        Preconditions.checkArgument(duration > 0, "Potion duration may not be inferior to 1");
        return duration(duration, TimeUnit.SECONDS);
    }

    public PotionEffectBuilder duration(long duration, TimeUnit timeUnit) {
        this.duration = Ticks.from(duration, timeUnit);
        return this;
    }

    public PotionEffectBuilder amplifier(int amplifier) {
        Preconditions.checkArgument(duration > 0, "Potion amplifier may not be inferior to 1");
        this.amplifier = amplifier;
        return this;
    }

    public PotionEffect build() {
        return new PotionEffect(type, (int) duration, Math.min(amplifier - 1, 0));
    }
}
