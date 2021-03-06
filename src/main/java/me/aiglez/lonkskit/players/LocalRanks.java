package me.aiglez.lonkskit.players;

import me.aiglez.lonkskit.kits.Kit;
import me.aiglez.lonkskit.kits.KitRank;

import java.util.List;
import java.util.Optional;

public interface LocalRanks {
    List<KitRank> getRanks();

    Optional<KitRank> getRank(Kit kit);

    boolean hasRank(Kit kit);

    KitRank addRank(KitRank rank);

    boolean removeRank(KitRank rank);
}
