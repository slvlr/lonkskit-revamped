package me.aiglez.lonkskit.utils;

import me.lucko.helper.scheduler.Ticks;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.TimeUnit;

public class PotionEffectBuilder {

    public static PotionEffectBuilder newBuilder() {
        return new PotionEffectBuilder();
    }

    private PotionEffectType type;
    private long duration = 1L;
    private int amplifier = 1;

    public PotionEffectBuilder type(PotionEffectType type) {
        this.type = type;
        return this;
    }

    public PotionEffectBuilder type(String name) {
        final PotionEffectType type = PotionEffectType.getByName(name);
        if(type == null) {
            Logger.debug("Couldn't find any Potion Effect Type matching (" + name + ")");
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
        this.duration = duration;
        return this;
    }

    public PotionEffectBuilder duration(long duration, TimeUnit timeUnit) {
        this.duration = Ticks.from(duration, timeUnit);
        return this;
    }

    public PotionEffectBuilder amplifier(int amplifier) {
        this.amplifier = amplifier;
        return this;
    }

    public PotionEffect build() {
        return new PotionEffect(type, (int) duration, amplifier - 1);
    }
}
