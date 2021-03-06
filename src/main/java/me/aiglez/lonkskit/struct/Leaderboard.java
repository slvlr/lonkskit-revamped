
package me.aiglez.lonkskit.struct;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import me.aiglez.lonkskit.KitPlugin;
import me.aiglez.lonkskit.utils.Locations;
import me.aiglez.lonkskit.utils.Logger;
import me.lucko.helper.Schedulers;
import me.lucko.helper.scheduler.Ticks;
import me.lucko.helper.text3.Text;
import org.bukkit.Location;

import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class Leaderboard<T> {

    protected final String name, format;
    protected final long refreshRateTicks;
    protected Hologram hologram;
    protected List<TextLine> lines;
    protected List<T> cache;

    public static final int SIZE = 10;

    public Leaderboard(final String name, final Location location, final long refreshRate, final TimeUnit timeUnit, final String format) {
        Preconditions.checkNotNull(name, "name may not be null");
        Preconditions.checkNotNull(location, "location may not be null");
        Preconditions.checkNotNull(timeUnit, "time unit may not be null");
        Preconditions.checkNotNull(format, "format may not be null");
        this.name = name;
        this.refreshRateTicks = Ticks.from(refreshRate, timeUnit);
        this.format = Text.colorize(format);

        Logger.debug("Creating a hologram [{0}] with refresh rate of ({1} {2}) at [{3}]", this.name, refreshRate, timeUnit.name(), Locations.toString(location));
        this.hologram = HologramsAPI.createHologram(Helper.hostPlugin(), location);
        this.hologram.appendTextLine("§8§m--------------------------");
        this.lines = Lists.newArrayList();
        for (int i = 0; i < SIZE; i++) {
            this.lines.add(this.hologram.appendTextLine(this.format));
        }
        this.hologram.appendTextLine("§8§m--------------------------");

        Schedulers.builder()
                .sync()
                .after(10)
                .every(this.refreshRateTicks)
                .run(this::reloadCache);

        Schedulers.builder()
                .sync()
                .after(12)
                .every(this.refreshRateTicks + 2)
                .run(this::reloadView);
    }

    public abstract void reloadCache();

    public abstract Hologram reloadView();

}
