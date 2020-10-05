package me.aiglez.lonkskit.players;

import me.aiglez.lonkskit.kits.Kit;

import java.util.List;
import java.util.Optional;

public interface LocalRenter {

    List<LocalRent> getRents();

    Optional<LocalRent> getRent(Kit kit);

    boolean hasRented(Kit kit);

    void addRent(LocalRent rent);

    void removeRent(LocalRent rent);
}
