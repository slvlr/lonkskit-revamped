package me.aiglez.lonkskit.utils;

import com.google.common.base.Preconditions;
import me.aiglez.lonkskit.WorldProvider;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Location;

import java.util.Optional;

public class Locations {

    public static String toString(final Location location) {
        Preconditions.checkNotNull(location, "location may not be null");
        return location.getX() + ", " + location.getY() + ", " + location.getZ();
    }

    public static Optional<Location> fromString(final String serialized) {
        Preconditions.checkNotNull(serialized, "serialized location may not be null");
        final String[] split = serialized.split(", ");
        if(split.length < 3) {
            return Optional.empty();
        }
        return Optional.of(new Location(WorldProvider.KP_WORLD, NumberUtils.toDouble(split[0]), NumberUtils.toDouble(split[1]), NumberUtils.toDouble(split[2])));
    }
}
